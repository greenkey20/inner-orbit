package com.greenkey20.innerorbit.weeklyreport;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.LogQueryPort;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportAiPort;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportRepository;
import com.greenkey20.innerorbit.weeklyreport.application.service.WeeklyReportGenerator;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * WeeklyReportGenerator 단위 테스트
 * 핵심 비즈니스 로직: 로그 수에 따른 상태 결정 (NA / INSUFFICIENT / GENERATED)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WeeklyReportGenerator 테스트")
class WeeklyReportGeneratorTest {

    @Mock
    private LogQueryPort logQueryPort;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private WeeklyReportAiPort weeklyReportAiPort;

    @InjectMocks
    private WeeklyReportGenerator weeklyReportGenerator;

    private static final Long USER_ID = 1L;
    private static final LocalDate WEEK_START = LocalDate.of(2026, 3, 9);   // 월요일
    private static final LocalDate WEEK_END   = LocalDate.of(2026, 3, 15);  // 일요일 전날(토)

    @BeforeEach
    void setUp() {
        given(weeklyReportRepository.save(any(WeeklyReport.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
    }

    // -----------------------------------------------------------------------
    // 0건 → NA
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그 0건 → status=NA, report=null, AI 호출 없음")
    void generateReportForUser_NoLogs_StatusNA() {
        given(logQueryPort.findByUserIdAndCreatedAtBetween(eq(USER_ID), any(), any()))
                .willReturn(List.of());

        WeeklyReport result = weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        assertThat(result.getStatus()).isEqualTo(WeeklyReportStatus.NA);
        assertThat(result.getLogCount()).isEqualTo(0);
        assertThat(result.getReport()).isNull();
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getWeekStart()).isEqualTo(WEEK_START);
        assertThat(result.getWeekEnd()).isEqualTo(WEEK_END);

        verifyNoInteractions(weeklyReportAiPort);
    }

    // -----------------------------------------------------------------------
    // 1건 → INSUFFICIENT
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그 1건 → status=INSUFFICIENT, report=null, AI 호출 없음")
    void generateReportForUser_OneLog_StatusInsufficient() {
        LogEntry singleLog = dailyLog("오늘 하루 기록", 70, 30);
        given(logQueryPort.findByUserIdAndCreatedAtBetween(eq(USER_ID), any(), any()))
                .willReturn(List.of(singleLog));

        WeeklyReport result = weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        assertThat(result.getStatus()).isEqualTo(WeeklyReportStatus.INSUFFICIENT);
        assertThat(result.getLogCount()).isEqualTo(1);
        assertThat(result.getReport()).isNull();

        verifyNoInteractions(weeklyReportAiPort);
    }

    // -----------------------------------------------------------------------
    // 2건 이상 → GENERATED
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("로그 2건 이상 → status=GENERATED, AI 호출 1회, report 저장")
    void generateReportForUser_MultipleLogs_StatusGenerated() {
        List<LogEntry> logs = List.of(
                dailyLog("첫째 날 기록", 70, 40),
                dailyLog("둘째 날 기록", 60, 50)
        );
        WeeklyReportContent aiContent = new WeeklyReportContent(
                "이번 주 흐름 요약", "패턴 분석", "회복력 평가", "다음 주 제안"
        );

        given(logQueryPort.findByUserIdAndCreatedAtBetween(eq(USER_ID), any(), any()))
                .willReturn(logs);
        given(weeklyReportAiPort.generateWeeklyReport(any(String.class)))
                .willReturn(aiContent);

        WeeklyReport result = weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        assertThat(result.getStatus()).isEqualTo(WeeklyReportStatus.GENERATED);
        assertThat(result.getLogCount()).isEqualTo(2);
        assertThat(result.getReport()).isNotNull();
        assertThat(result.getReport().getWeeklyFlow()).isEqualTo("이번 주 흐름 요약");
        assertThat(result.getReport().getPatterns()).isEqualTo("패턴 분석");
        assertThat(result.getReport().getResilience()).isEqualTo("회복력 평가");
        assertThat(result.getReport().getRecommendations()).isEqualTo("다음 주 제안");

        verify(weeklyReportAiPort, times(1)).generateWeeklyReport(any());
    }

    @Test
    @DisplayName("로그 7건 → logCount=7, AI 정확히 1회 호출")
    void generateReportForUser_SevenLogs_AiCalledOnce() {
        List<LogEntry> logs = List.of(
                dailyLog("월", 70, 30), dailyLog("화", 65, 35), dailyLog("수", 75, 25),
                dailyLog("목", 80, 20), dailyLog("금", 60, 45), dailyLog("토", 55, 50),
                dailyLog("일", 70, 40)
        );
        given(logQueryPort.findByUserIdAndCreatedAtBetween(eq(USER_ID), any(), any()))
                .willReturn(logs);
        given(weeklyReportAiPort.generateWeeklyReport(any()))
                .willReturn(new WeeklyReportContent("흐름", "패턴", "회복력", "제안"));

        WeeklyReport result = weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        assertThat(result.getLogCount()).isEqualTo(7);
        assertThat(result.getStatus()).isEqualTo(WeeklyReportStatus.GENERATED);
        verify(weeklyReportAiPort, times(1)).generateWeeklyReport(any());
    }

    // -----------------------------------------------------------------------
    // 날짜 범위 쿼리 파라미터 검증
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("logQueryPort 호출 시 weekStart 00:00:00 ~ weekEnd 23:59:59 범위로 쿼리")
    void generateReportForUser_CorrectDateRange() {
        given(logQueryPort.findByUserIdAndCreatedAtBetween(any(), any(), any()))
                .willReturn(List.of());

        weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        ArgumentCaptor<LocalDateTime> fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCaptor   = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(logQueryPort).findByUserIdAndCreatedAtBetween(eq(USER_ID), fromCaptor.capture(), toCaptor.capture());

        assertThat(fromCaptor.getValue()).isEqualTo(WEEK_START.atStartOfDay());
        assertThat(toCaptor.getValue()).isEqualTo(WEEK_END.atTime(23, 59, 59));
    }

    // -----------------------------------------------------------------------
    // AI 포맷 검증 (formatLogsForAi)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DAILY 로그 포맷 — [날짜] 안정성:N, 중력:N / 내용")
    void formatLogsForAi_DailyLog_CorrectFormat() {
        LogEntry log = dailyLog("오늘 하루 기록", 75, 40);
        List<LogEntry> logs = List.of(log, dailyLog("둘째 날", 60, 50));

        given(logQueryPort.findByUserIdAndCreatedAtBetween(any(), any(), any()))
                .willReturn(logs);

        ArgumentCaptor<String> formattedCaptor = ArgumentCaptor.forClass(String.class);
        given(weeklyReportAiPort.generateWeeklyReport(formattedCaptor.capture()))
                .willReturn(new WeeklyReportContent("흐름", "패턴", "회복력", "제안"));

        weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        String formatted = formattedCaptor.getValue();
        assertThat(formatted).contains("안정성:75");
        assertThat(formatted).contains("중력:40");
        assertThat(formatted).contains("오늘 하루 기록");
    }

    @Test
    @DisplayName("SENSORY 로그 포맷 — [날짜] 장소:X / 시각:X, 청각:X, 촉각:X")
    void formatLogsForAi_SensoryLog_CorrectFormat() {
        LogEntry log = sensoryLog("무이네 해변", "황금빛 모래", "파도 소리", "따뜻한 모래");
        LogEntry log2 = dailyLog("추가 로그", 60, 40);

        given(logQueryPort.findByUserIdAndCreatedAtBetween(any(), any(), any()))
                .willReturn(List.of(log, log2));

        ArgumentCaptor<String> formattedCaptor = ArgumentCaptor.forClass(String.class);
        given(weeklyReportAiPort.generateWeeklyReport(formattedCaptor.capture()))
                .willReturn(new WeeklyReportContent("흐름", "패턴", "회복력", "제안"));

        weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        String formatted = formattedCaptor.getValue();
        assertThat(formatted).contains("장소:무이네 해변");
        assertThat(formatted).contains("시각:황금빛 모래");
        assertThat(formatted).contains("청각:파도 소리");
        assertThat(formatted).contains("촉각:따뜻한 모래");
    }

    @Test
    @DisplayName("INSIGHT 로그 포맷 — [날짜] 관찰:X → CS개념:X → 적용:X")
    void formatLogsForAi_InsightLog_CorrectFormat() {
        LogEntry log = insightLog("오토바이 행렬 관찰", "Message Queue", "비동기 처리 적용");
        LogEntry log2 = dailyLog("추가 로그", 60, 40);

        given(logQueryPort.findByUserIdAndCreatedAtBetween(any(), any(), any()))
                .willReturn(List.of(log, log2));

        ArgumentCaptor<String> formattedCaptor = ArgumentCaptor.forClass(String.class);
        given(weeklyReportAiPort.generateWeeklyReport(formattedCaptor.capture()))
                .willReturn(new WeeklyReportContent("흐름", "패턴", "회복력", "제안"));

        weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        String formatted = formattedCaptor.getValue();
        assertThat(formatted).contains("관찰:오토바이 행렬 관찰");
        assertThat(formatted).contains("CS개념:Message Queue");
        assertThat(formatted).contains("적용:비동기 처리 적용");
    }

    @Test
    @DisplayName("repository.save()가 정확히 1번 호출되고 올바른 WeeklyReport 전달")
    void generateReportForUser_SaveCalledOnceWithCorrectData() {
        given(logQueryPort.findByUserIdAndCreatedAtBetween(any(), any(), any()))
                .willReturn(List.of());

        weeklyReportGenerator.generateReportForUser(USER_ID, WEEK_START, WEEK_END);

        ArgumentCaptor<WeeklyReport> captor = ArgumentCaptor.forClass(WeeklyReport.class);
        verify(weeklyReportRepository, times(1)).save(captor.capture());

        WeeklyReport saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getWeekStart()).isEqualTo(WEEK_START);
        assertThat(saved.getWeekEnd()).isEqualTo(WEEK_END);
    }

    // -----------------------------------------------------------------------
    // 헬퍼 메서드
    // -----------------------------------------------------------------------

    private LogEntry dailyLog(String content, int stability, int gravity) {
        return LogEntry.builder()
                .id(System.nanoTime())
                .userId(USER_ID)
                .logType(LogType.DAILY)
                .content(content)
                .stability(stability)
                .gravity(gravity)
                .createdAt(WEEK_START.atTime(10, 0))
                .build();
    }

    private LogEntry sensoryLog(String location, String visual, String auditory, String tactile) {
        return LogEntry.builder()
                .id(System.nanoTime())
                .userId(USER_ID)
                .logType(LogType.SENSORY)
                .stability(70)
                .gravity(30)
                .location(location)
                .sensoryVisual(visual)
                .sensoryAuditory(auditory)
                .sensoryTactile(tactile)
                .createdAt(WEEK_START.atTime(11, 0))
                .build();
    }

    private LogEntry insightLog(String observation, String abstraction, String application) {
        return LogEntry.builder()
                .id(System.nanoTime())
                .userId(USER_ID)
                .logType(LogType.INSIGHT)
                .content(observation)
                .stability(80)
                .gravity(20)
                .insightAbstraction(abstraction)
                .insightApplication(application)
                .createdAt(WEEK_START.atTime(12, 0))
                .build();
    }
}
