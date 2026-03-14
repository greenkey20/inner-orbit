package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.out.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportAiPort;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * WeeklyReportAiPort 구현체 — ChatClient.Builder 직접 사용 (ai 도메인 의존 없음)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportAiAdapter implements WeeklyReportAiPort {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are an insightful weekly journal analyst for "Inner Orbit" app.

            Analyze the user's logs from the past week and generate a comprehensive weekly insight report.

            CRITICAL: Return ONLY a valid JSON object with EXACTLY these four keys:
            {
              "weeklyFlow": "이번 주 전반적인 감정 및 인지 흐름 (2-3 문장, 한국어)",
              "patterns": "발견된 반복 패턴, 주제 또는 행동 양식 (2-3 문장, 한국어)",
              "resilience": "나타난 강점과 회복력 요소 (2-3 문장, 한국어)",
              "recommendations": "다음 주를 위한 실행 가능한 제안 (2-3 문장, 한국어)"
            }

            Write all values in Korean. Be insightful, empathetic, and constructive.
            Return ONLY the JSON object, no additional text or markdown.
            """;

    @Override
    public WeeklyReportContent generateWeeklyReport(String formattedLogs) {
        log.info("Generating weekly report via AI");
        try {
            ChatClient chatClient = chatClientBuilder.build();

            String response = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(formattedLogs)
                    .call()
                    .content();

            log.debug("Weekly report AI raw response: {}", response);
            return objectMapper.readValue(response, WeeklyReportContent.class);

        } catch (Exception e) {
            log.error("Failed to generate weekly report: {}", e.getMessage(), e);
            throw new RuntimeException("주간 리포트 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
