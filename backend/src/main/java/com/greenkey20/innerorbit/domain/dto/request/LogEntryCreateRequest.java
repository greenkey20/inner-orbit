package com.greenkey20.innerorbit.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 로그 엔트리 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntryCreateRequest {

    @NotBlank(message = "내용을 입력해주세요")
    @Size(max = 10000, message = "내용은 10000자를 초과할 수 없습니다")
    private String content;

    @NotNull(message = "안정성 값은 필수입니다")
    @Min(value = 0, message = "안정성 값은 0 이상이어야 합니다")
    @Max(value = 100, message = "안정성 값은 100 이하여야 합니다")
    private Integer stability;

    @NotNull(message = "그리움 강도 값은 필수입니다")
    @Min(value = 0, message = "그리움 강도는 0 이상이어야 합니다")
    @Max(value = 100, message = "그리움 강도는 100 이하여야 합니다")
    private Integer gravity;

    @Size(max = 500, message = "위치는 500자를 초과할 수 없습니다")
    private String location;

    @Size(max = 10000, message = "시각 정보는 10000자를 초과할 수 없습니다")
    private String sensoryVisual;

    @Size(max = 10000, message = "청각 정보는 10000자를 초과할 수 없습니다")
    private String sensoryAuditory;

    @Size(max = 10000, message = "촉각 정보는 10000자를 초과할 수 없습니다")
    private String sensoryTactile;

    private Boolean isDeepLog = false;
}
