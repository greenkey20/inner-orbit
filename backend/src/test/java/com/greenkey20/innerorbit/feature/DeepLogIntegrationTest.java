package com.greenkey20.innerorbit.feature;

import com.greenkey20.innerorbit.domain.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.domain.entity.LogEntry;
import com.greenkey20.innerorbit.repository.LogRepository;
import com.greenkey20.innerorbit.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Deep Log 기능 통합 테스트
 * 새로운 sensory field들과 isDeepLog flag의 persistence 및 retrieval을 검증
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Deep Log Integration Test")
class DeepLogIntegrationTest {

    @Autowired
    private LogService logService;

    @Autowired
    private LogRepository logRepository;

    private LogEntryCreateRequest deepLogRequest;

    @BeforeEach
    void setUp() {
        // Deep Log 요청 데이터 준비
        deepLogRequest = LogEntryCreateRequest.builder()
                .content("오늘 무이네 백사장에서의 여행 경험을 기록한다.")
                .stability(85)
                .gravity(25)
                .location("Mui Ne White Sand Dunes")
                .sensoryVisual("Golden sunrise")
                .sensoryAuditory("Wind blowing")
                .sensoryTactile("Soft sand")
                .isDeepLog(true)
                .build();
    }

    @Test
    @DisplayName("Deep Log 생성 및 조회 - 모든 새로운 필드가 정상적으로 저장되고 조회되는지 검증")
    void createAndRetrieveDeepLog_Success() {
        // Step 1: Deep Log 생성
        LogEntryResponse createdResponse = logService.createLogEntry(deepLogRequest);

        // Step 2: 생성 결과 검증
        assertThat(createdResponse).isNotNull();
        assertThat(createdResponse.getId()).isNotNull();
        assertThat(createdResponse.getLocation()).isEqualTo("Mui Ne White Sand Dunes");
        assertThat(createdResponse.getSensoryVisual()).isEqualTo("Golden sunrise");
        assertThat(createdResponse.getSensoryAuditory()).isEqualTo("Wind blowing");
        assertThat(createdResponse.getSensoryTactile()).isEqualTo("Soft sand");
        assertThat(createdResponse.getIsDeepLog()).isTrue();
        
        // Core fields should also be correct
        assertThat(createdResponse.getContent()).isEqualTo("오늘 무이네 백사장에서의 여행 경험을 기록한다.");
        assertThat(createdResponse.getStability()).isEqualTo(85);
        assertThat(createdResponse.getGravity()).isEqualTo(25);

        // Step 3: 데이터베이스에서 직접 조회하여 검증
        LogEntry savedEntity = logRepository.findById(createdResponse.getId())
                .orElseThrow(() -> new AssertionError("저장된 엔티티를 찾을 수 없습니다."));

        assertThat(savedEntity.getLocation()).isEqualTo("Mui Ne White Sand Dunes");
        assertThat(savedEntity.getSensoryVisual()).isEqualTo("Golden sunrise");
        assertThat(savedEntity.getSensoryAuditory()).isEqualTo("Wind blowing");
        assertThat(savedEntity.getSensoryTactile()).isEqualTo("Soft sand");
        assertThat(savedEntity.getIsDeepLog()).isTrue();

        // Step 4: 서비스를 통한 조회로 재검증
        LogEntryResponse retrievedResponse = logService.getLogEntry(createdResponse.getId());

        assertThat(retrievedResponse.getLocation()).isEqualTo("Mui Ne White Sand Dunes");
        assertThat(retrievedResponse.getSensoryVisual()).isEqualTo("Golden sunrise");
        assertThat(retrievedResponse.getSensoryAuditory()).isEqualTo("Wind blowing");
        assertThat(retrievedResponse.getSensoryTactile()).isEqualTo("Soft sand");
        assertThat(retrievedResponse.getIsDeepLog()).isTrue();
    }

    @Test
    @DisplayName("일반 Log 생성 - isDeepLog가 false로 기본값 설정되는지 검증")
    void createRegularLog_DefaultIsDeepLogFalse() {
        // 일반 로그 요청 (Deep Log 필드 없이)
        LogEntryCreateRequest regularRequest = LogEntryCreateRequest.builder()
                .content("일반 감정 로그입니다.")
                .stability(70)
                .gravity(50)
                .build();

        // 생성 및 검증
        LogEntryResponse response = logService.createLogEntry(regularRequest);

        assertThat(response.getIsDeepLog()).isFalse();
        assertThat(response.getLocation()).isNull();
        assertThat(response.getSensoryVisual()).isNull();
        assertThat(response.getSensoryAuditory()).isNull();
        assertThat(response.getSensoryTactile()).isNull();
    }

    @Test
    @DisplayName("Deep Log with null sensory fields - 일부 sensory 필드가 null인 경우 처리")
    void createDeepLogWithNullSensoryFields_Success() {
        LogEntryCreateRequest partialDeepLogRequest = LogEntryCreateRequest.builder()
                .content("부분적인 감각 정보만 있는 Deep Log")
                .stability(60)
                .gravity(40)
                .location("Seoul, Korea")
                .sensoryVisual("Beautiful cherry blossoms")
                .sensoryAuditory(null) // null
                .sensoryTactile("Cool spring breeze")
                .isDeepLog(true)
                .build();

        LogEntryResponse response = logService.createLogEntry(partialDeepLogRequest);

        assertThat(response.getIsDeepLog()).isTrue();
        assertThat(response.getLocation()).isEqualTo("Seoul, Korea");
        assertThat(response.getSensoryVisual()).isEqualTo("Beautiful cherry blossoms");
        assertThat(response.getSensoryAuditory()).isNull();
        assertThat(response.getSensoryTactile()).isEqualTo("Cool spring breeze");
    }

    @Test
    @DisplayName("Location 길이 제한 테스트 - 500자 이내 위치 정보 저장")
    void createDeepLogWithLongLocation_Success() {
        String longLocation = "Very long location name that could potentially exceed normal limits but should still be within 500 characters: " +
                "Mui Ne White Sand Dunes, Phan Thiet, Binh Thuan Province, Vietnam - A beautiful coastal resort town known for its stunning red and white sand dunes";

        LogEntryCreateRequest longLocationRequest = LogEntryCreateRequest.builder()
                .content("긴 위치 이름 테스트")
                .stability(75)
                .gravity(30)
                .location(longLocation)
                .sensoryVisual("Spectacular dunes stretching to the horizon")
                .isDeepLog(true)
                .build();

        LogEntryResponse response = logService.createLogEntry(longLocationRequest);

        assertThat(response.getLocation()).isEqualTo(longLocation);
        assertThat(response.getLocation().length()).isLessThanOrEqualTo(500);
    }

    @Test
    @DisplayName("Sensory 필드 긴 텍스트 저장 테스트 - TEXT 컬럼 활용")
    void createDeepLogWithLongSensoryText_Success() {
        String longSensoryText = "A very detailed sensory description that captures the full richness of the experience: " +
                "The golden sand stretches endlessly under the morning sun, creating rippling patterns that dance with the wind. " +
                "Each grain feels warm and soft beneath my feet, shifting and flowing like liquid silk. The texture changes " +
                "subtly as I walk from the fine, almost powder-like sand near the dunes to the slightly coarser grains " +
                "where the wind has carved deeper channels. The warmth penetrates through my skin, creating a soothing " +
                "sensation that connects me deeply to this moment in time.";

        LogEntryCreateRequest longSensoryRequest = LogEntryCreateRequest.builder()
                .content("상세한 감각 기록")
                .stability(90)
                .gravity(15)
                .location("Mui Ne")
                .sensoryTactile(longSensoryText)
                .isDeepLog(true)
                .build();

        LogEntryResponse response = logService.createLogEntry(longSensoryRequest);

        assertThat(response.getSensoryTactile()).isEqualTo(longSensoryText);
        assertThat(response.getSensoryTactile().length()).isGreaterThan(1000);
    }
}