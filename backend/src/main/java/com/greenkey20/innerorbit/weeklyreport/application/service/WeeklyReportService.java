package com.greenkey20.innerorbit.weeklyreport.application.service;

import com.greenkey20.innerorbit.auth.application.port.out.UserRepository;
import com.greenkey20.innerorbit.auth.domain.model.User;
import com.greenkey20.innerorbit.common.exception.BusinessException;
import com.greenkey20.innerorbit.common.exception.ErrorCode;
import com.greenkey20.innerorbit.weeklyreport.application.port.in.WeeklyReportUseCase;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportRepository;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * WeeklyReportUseCase 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportService implements WeeklyReportUseCase {

    private final WeeklyReportRepository weeklyReportRepository;
    private final WeeklyReportGenerator weeklyReportGenerator;
    private final UserRepository userRepository;

    /**
     * 스케줄러 전용 — 전체 사용자 주간 리포트 일괄 생성
     */
    public void generateForAllUsers(LocalDate weekStart, LocalDate weekEnd) {
        List<User> users = userRepository.findAll();
        log.info("Starting weekly report generation for {} users, week: {} ~ {}", users.size(), weekStart, weekEnd);

        for (User user : users) {
            try {
                weeklyReportGenerator.generateReportForUser(user.getId(), weekStart, weekEnd);
                log.info("Weekly report generated for userId={}", user.getId());
            } catch (Exception e) {
                log.error("Failed to generate weekly report for userId={}: {}", user.getId(), e.getMessage(), e);
            }
        }

        log.info("Weekly report generation completed for all users");
    }

    @Override
    public List<WeeklyReport> getMyReports(Long userId) {
        return weeklyReportRepository.findAllByUserIdOrderByWeekStartDesc(userId);
    }

    @Override
    public WeeklyReport getReportById(Long id, Long userId) {
        return weeklyReportRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WEEKLY_REPORT_NOT_FOUND));
    }

    @Override
    public WeeklyReport generateForCurrentWeek(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate weekStart = yesterday.with(DayOfWeek.MONDAY);

        if (weeklyReportRepository.existsByUserIdAndWeekStart(userId, weekStart)) {
            log.info("Weekly report already exists for userId={}, weekStart={}", userId, weekStart);
            return weeklyReportRepository.findByUserIdAndWeekStart(userId, weekStart)
                    .orElseThrow(() -> new BusinessException(ErrorCode.WEEKLY_REPORT_NOT_FOUND));
        }

        return weeklyReportGenerator.generateReportForUser(userId, weekStart, yesterday);
    }
}
