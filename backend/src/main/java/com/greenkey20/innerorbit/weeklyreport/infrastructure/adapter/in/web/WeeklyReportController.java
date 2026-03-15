package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.in.web;

import com.greenkey20.innerorbit.security.UserPrincipal;
import com.greenkey20.innerorbit.weeklyreport.application.port.in.WeeklyReportUseCase;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.in.web.dto.WeeklyReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주간 리포트 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/weekly-reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WeeklyReportController {

    private final WeeklyReportUseCase weeklyReportUseCase;

    @GetMapping
    public ResponseEntity<List<WeeklyReportResponse>> getMyReports(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).userId();
        log.info("Fetching weekly reports for userId={}", userId);
        List<WeeklyReport> reports = weeklyReportUseCase.getMyReports(userId);
        List<WeeklyReportResponse> responses = reports.stream()
                .map(WeeklyReportResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeeklyReportResponse> getReport(
            @PathVariable Long id, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).userId();
        log.info("Fetching weekly report id={} for userId={}", id, userId);
        WeeklyReport report = weeklyReportUseCase.getReportById(id, userId);
        return ResponseEntity.ok(WeeklyReportResponse.from(report));
    }

    @PostMapping("/generate")
    public ResponseEntity<WeeklyReportResponse> generateForCurrentWeek(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).userId();
        log.info("Manual weekly report generation triggered for userId={}", userId);
        WeeklyReport report = weeklyReportUseCase.generateForCurrentWeek(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(WeeklyReportResponse.from(report));
    }
}
