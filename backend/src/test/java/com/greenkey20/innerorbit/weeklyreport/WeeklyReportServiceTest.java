package com.greenkey20.innerorbit.weeklyreport;

import com.greenkey20.innerorbit.auth.application.port.out.UserRepository;
import com.greenkey20.innerorbit.auth.domain.model.User;
import com.greenkey20.innerorbit.common.exception.BusinessException;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportRepository;
import com.greenkey20.innerorbit.weeklyreport.application.service.WeeklyReportGenerator;
import com.greenkey20.innerorbit.weeklyreport.application.service.WeeklyReportService;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * WeeklyReportService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WeeklyReportService 테스트")
class WeeklyReportServiceTest {

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private WeeklyReportGenerator weeklyReportGenerator;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WeeklyReportService weeklyReportService;

    private static final Long USER_ID = 1L;

    // -----------------------------------------------------------------------
    // getMyReports
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getMyReports — repository에서 반환된 목록을 그대로 반환")
    void getMyReports_ReturnsList() {
        WeeklyReport r1 = report(1L, WeeklyReportStatus.GENERATED);
        WeeklyReport r2 = report(2L, WeeklyReportStatus.NA);
        given(weeklyReportRepository.findAllByUserIdOrderByWeekStartDesc(USER_ID))
                .willReturn(List.of(r1, r2));

        List<WeeklyReport> result = weeklyReportService.getMyReports(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("getMyReports — 리포트 없으면 빈 리스트 반환")
    void getMyReports_EmptyList() {
        given(weeklyReportRepository.findAllByUserIdOrderByWeekStartDesc(USER_ID))
                .willReturn(List.of());

        List<WeeklyReport> result = weeklyReportService.getMyReports(USER_ID);

        assertThat(result).isEmpty();
    }

    // -----------------------------------------------------------------------
    // getReportById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("getReportById — 존재하면 반환")
    void getReportById_Found_ReturnsReport() {
        WeeklyReport existing = report(10L, WeeklyReportStatus.GENERATED);
        given(weeklyReportRepository.findByIdAndUserId(10L, USER_ID))
                .willReturn(Optional.of(existing));

        WeeklyReport result = weeklyReportService.getReportById(10L, USER_ID);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(WeeklyReportStatus.GENERATED);
    }

    @Test
    @DisplayName("getReportById — 없으면 BusinessException (WEEKLY_REPORT_NOT_FOUND)")
    void getReportById_NotFound_ThrowsException() {
        given(weeklyReportRepository.findByIdAndUserId(99L, USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> weeklyReportService.getReportById(99L, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("주간 리포트");
    }

    @Test
    @DisplayName("getReportById — 다른 사용자 소유 리포트도 NOT_FOUND로 처리 (userId 불일치)")
    void getReportById_DifferentUser_ThrowsException() {
        given(weeklyReportRepository.findByIdAndUserId(10L, 999L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> weeklyReportService.getReportById(10L, 999L))
                .isInstanceOf(BusinessException.class);
    }

    // -----------------------------------------------------------------------
    // generateForCurrentWeek
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateForCurrentWeek — 이미 존재하면 generator 호출 없이 기존 리포트 반환")
    void generateForCurrentWeek_AlreadyExists_ReturnsExisting() {
        LocalDate weekEnd   = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDate weekStart = weekEnd.minusDays(6);
        WeeklyReport existing = report(5L, WeeklyReportStatus.GENERATED);

        given(weeklyReportRepository.existsByUserIdAndWeekStart(USER_ID, weekStart)).willReturn(true);
        given(weeklyReportRepository.findByUserIdAndWeekStart(USER_ID, weekStart))
                .willReturn(Optional.of(existing));

        WeeklyReport result = weeklyReportService.generateForCurrentWeek(USER_ID);

        assertThat(result.getId()).isEqualTo(5L);
        verifyNoInteractions(weeklyReportGenerator);
    }

    @Test
    @DisplayName("generateForCurrentWeek — 없으면 generator 위임하고 결과 반환")
    void generateForCurrentWeek_NotExists_DelegatesToGenerator() {
        LocalDate weekEnd   = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
        LocalDate weekStart = weekEnd.minusDays(6);
        WeeklyReport generated = report(6L, WeeklyReportStatus.GENERATED);

        given(weeklyReportRepository.existsByUserIdAndWeekStart(USER_ID, weekStart)).willReturn(false);
        given(weeklyReportGenerator.generateReportForUser(eq(USER_ID), eq(weekStart), eq(weekEnd)))
                .willReturn(generated);

        WeeklyReport result = weeklyReportService.generateForCurrentWeek(USER_ID);

        assertThat(result.getId()).isEqualTo(6L);
        verify(weeklyReportGenerator).generateReportForUser(USER_ID, weekStart, weekEnd);
    }

    // -----------------------------------------------------------------------
    // generateForAllUsers
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("generateForAllUsers — 전체 사용자 수만큼 generator 호출")
    void generateForAllUsers_CallsGeneratorForEachUser() {
        User u1 = User.builder().id(1L).username("alice").build();
        User u2 = User.builder().id(2L).username("bob").build();
        User u3 = User.builder().id(3L).username("carol").build();
        given(userRepository.findAll()).willReturn(List.of(u1, u2, u3));

        LocalDate weekStart = LocalDate.of(2026, 3, 9);
        LocalDate weekEnd   = LocalDate.of(2026, 3, 15);
        given(weeklyReportGenerator.generateReportForUser(any(), any(), any()))
                .willReturn(report(null, WeeklyReportStatus.GENERATED));

        weeklyReportService.generateForAllUsers(weekStart, weekEnd);

        verify(weeklyReportGenerator).generateReportForUser(1L, weekStart, weekEnd);
        verify(weeklyReportGenerator).generateReportForUser(2L, weekStart, weekEnd);
        verify(weeklyReportGenerator).generateReportForUser(3L, weekStart, weekEnd);
    }

    @Test
    @DisplayName("generateForAllUsers — 한 사용자 AI 실패 시 나머지 사용자 계속 진행")
    void generateForAllUsers_OneUserFails_ContinuesForOthers() {
        User u1 = User.builder().id(1L).username("alice").build();
        User u2 = User.builder().id(2L).username("bob").build();
        given(userRepository.findAll()).willReturn(List.of(u1, u2));

        given(weeklyReportGenerator.generateReportForUser(eq(1L), any(), any()))
                .willThrow(new RuntimeException("AI 타임아웃"));
        given(weeklyReportGenerator.generateReportForUser(eq(2L), any(), any()))
                .willReturn(report(7L, WeeklyReportStatus.GENERATED));

        LocalDate weekStart = LocalDate.of(2026, 3, 9);
        LocalDate weekEnd   = LocalDate.of(2026, 3, 15);

        // 예외가 전파되지 않고 정상 완료
        weeklyReportService.generateForAllUsers(weekStart, weekEnd);

        verify(weeklyReportGenerator).generateReportForUser(1L, weekStart, weekEnd);
        verify(weeklyReportGenerator).generateReportForUser(2L, weekStart, weekEnd);
    }

    // -----------------------------------------------------------------------
    // 헬퍼
    // -----------------------------------------------------------------------

    private WeeklyReport report(Long id, WeeklyReportStatus status) {
        return WeeklyReport.builder()
                .id(id)
                .userId(USER_ID)
                .weekStart(LocalDate.of(2026, 3, 9))
                .weekEnd(LocalDate.of(2026, 3, 15))
                .status(status)
                .logCount(status == WeeklyReportStatus.NA ? 0 : 3)
                .report(null)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
