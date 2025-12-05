package com.greenkey20.innerorbit.service;

import com.greenkey20.innerorbit.domain.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.domain.entity.LogEntry;
import com.greenkey20.innerorbit.exception.BusinessException;
import com.greenkey20.innerorbit.exception.ErrorCode;
import com.greenkey20.innerorbit.repository.LogRepository;
import com.greenkey20.innerorbit.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * LogService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LogService 테스트")
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogServiceImpl logService;

    private LogEntryCreateRequest validRequest;
    private LogEntry savedLogEntry;

    @BeforeEach
    void setUp() {
        // Given: 유효한 생성 요청 준비
        validRequest = LogEntryCreateRequest.builder()
                .content("오늘은 안정적인 하루였다. 그리움은 있지만 잘 관리하고 있다.")
                .stability(75)
                .gravity(40)
                .build();

        // Given: 저장될 엔티티 준비
        savedLogEntry = LogEntry.builder()
                .id(1L)
                .content(validRequest.getContent())
                .stability(validRequest.getStability())
                .gravity(validRequest.getGravity())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("정상적인 로그 엔트리 생성 - 성공")
    void createLogEntry_Success() {
        // Given
        given(logRepository.save(any(LogEntry.class))).willReturn(savedLogEntry);

        // When
        LogEntryResponse response = logService.createLogEntry(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getContent()).isEqualTo(validRequest.getContent());
        assertThat(response.getStability()).isEqualTo(validRequest.getStability());
        assertThat(response.getGravity()).isEqualTo(validRequest.getGravity());
        assertThat(response.getCreatedAt()).isNotNull();

        verify(logRepository).save(any(LogEntry.class));
    }

    @Test
    @DisplayName("내용이 비어있는 경우 - 실패 (Validation)")
    void createLogEntry_EmptyContent_Fail() {
        // Given: 빈 내용
        LogEntryCreateRequest emptyContentRequest = LogEntryCreateRequest.builder()
                .content("")
                .stability(50)
                .gravity(50)
                .build();

        // When & Then
        // Note: @Valid 어노테이션은 컨트롤러 레벨에서 작동하므로,
        // 서비스 레벨에서는 비즈니스 로직 검증을 수행
        assertThatThrownBy(() -> logService.createLogEntry(emptyContentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("내용을 입력해주세요");
    }

    @Test
    @DisplayName("안정성 값이 범위를 벗어난 경우 - 실패 (Validation)")
    void createLogEntry_InvalidStability_Fail() {
        // Given: 잘못된 안정성 값 (101)
        LogEntryCreateRequest invalidStabilityRequest = LogEntryCreateRequest.builder()
                .content("테스트 내용")
                .stability(101)
                .gravity(50)
                .build();

        // When & Then
        assertThatThrownBy(() -> logService.createLogEntry(invalidStabilityRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("안정성");
    }

    @Test
    @DisplayName("그리움 강도 값이 범위를 벗어난 경우 - 실패 (Validation)")
    void createLogEntry_InvalidGravity_Fail() {
        // Given: 잘못된 그리움 강도 값 (-1)
        LogEntryCreateRequest invalidGravityRequest = LogEntryCreateRequest.builder()
                .content("테스트 내용")
                .stability(50)
                .gravity(-1)
                .build();

        // When & Then
        assertThatThrownBy(() -> logService.createLogEntry(invalidGravityRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("그리움");
    }

    @Test
    @DisplayName("경계값 테스트 - 안정성 0, 그리움 강도 100 - 성공")
    void createLogEntry_BoundaryValues_Success() {
        // Given: 경계값
        LogEntryCreateRequest boundaryRequest = LogEntryCreateRequest.builder()
                .content("경계값 테스트")
                .stability(0)
                .gravity(100)
                .build();

        LogEntry boundaryEntry = LogEntry.builder()
                .id(2L)
                .content(boundaryRequest.getContent())
                .stability(boundaryRequest.getStability())
                .gravity(boundaryRequest.getGravity())
                .createdAt(LocalDateTime.now())
                .build();

        given(logRepository.save(any(LogEntry.class))).willReturn(boundaryEntry);

        // When
        LogEntryResponse response = logService.createLogEntry(boundaryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStability()).isEqualTo(0);
        assertThat(response.getGravity()).isEqualTo(100);

        verify(logRepository).save(any(LogEntry.class));
    }

    @Test
    @DisplayName("null 값 입력 - 실패")
    void createLogEntry_NullRequest_Fail() {
        // When & Then
        assertThatThrownBy(() -> logService.createLogEntry(null))
                .isInstanceOf(BusinessException.class);
    }
}
