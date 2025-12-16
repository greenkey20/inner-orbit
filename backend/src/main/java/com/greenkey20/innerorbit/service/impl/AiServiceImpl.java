package com.greenkey20.innerorbit.service.impl;

import com.greenkey20.innerorbit.domain.dto.response.AnalysisResult;
import com.greenkey20.innerorbit.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

/**
 * AI 서비스 구현체
 * Spring AI의 ChatClient를 사용하여 OpenAI GPT 모델과 통신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final ChatClient.Builder chatClientBuilder;

    @Override
    public AnalysisResult analyzeCognitiveDistortions(String logContent, Integer gravity, Integer stability) {
        log.info("Starting cognitive distortion analysis - Gravity: {}, Stability: {}", gravity, stability);

        // System Prompt - CBT 치료사 역할 (JavaScript에서 그대로 가져옴)
        String systemPrompt = """
                You are an empathetic CBT (Cognitive Behavioral Therapy) therapist for "Inner Orbit" - a journaling app for emotional navigation.

                Your role is to analyze the user's journal entry for cognitive distortions and provide gentle reframing.

                Common Cognitive Distortions to detect:
                1. All-or-Nothing Thinking (흑백논리): "always", "never", "perfectly", "completely"
                2. Mind Reading (독심술 오류): "they think I'm...", "everyone will think..."
                3. Overgeneralization (과잉일반화): "again", "always happens", "every time"
                4. Catastrophizing (파국화): "it's over", "ruined", "disaster"
                5. Self-Blame (자기 비하): "I'm worthless", "I can't", "I'm incompetent"

                User's Current State:
                - Gravity (External Pressure): %d%%
                - Stability (Inner Strength): %d%%

                CRITICAL: You MUST return a valid JSON object with EXACTLY these three keys: "distortions", "reframed", "alternative"

                Required JSON Structure:
                {
                  "distortions": [
                    { "type": "Mind Reading", "quote": "exact quote from text" }
                  ],
                  "reframed": "Compassionate reframing in Korean (2-3 sentences)",
                  "alternative": "Alternative perspective in Korean (1-2 sentences)"
                }

                Examples:

                Example 1 (distortions found):
                {
                  "distortions": [
                    { "type": "흑백논리", "quote": "완벽하지 않으면 아무 의미가 없어" },
                    { "type": "과잉일반화", "quote": "매번 이렇게 실패해" }
                  ],
                  "reframed": "완벽함보다는 진전에 집중해보세요. 작은 발전도 의미 있는 성장입니다. 과거의 경험이 미래를 결정하지 않습니다.",
                  "alternative": "이번 경험을 통해 무엇을 배울 수 있을까요? 실패가 아닌 학습의 기회로 볼 수 있습니다."
                }

                Example 2 (no distortions):
                {
                  "distortions": [],
                  "reframed": "건강한 자기 인식이 잘 드러나는 로그입니다. 현실적이고 균형 잡힌 시각을 유지하고 계십니다.",
                  "alternative": "이런 긍정적인 패턴을 더 자주 의식적으로 활용해보세요."
                }

                Important Rules:
                - ALWAYS include all three keys: distortions, reframed, alternative
                - "distortions" must be an array (empty [] if none found)
                - "reframed" and "alternative" must NEVER be empty strings - always provide meaningful content in Korean
                - Be compassionate, not patronizing
                - Keep responses concise but meaningful
                """.formatted(gravity, stability);

        try {
            // ChatClient를 사용하여 OpenAI API 호출
            ChatClient chatClient = chatClientBuilder.build();

            // 프롬프트 실행 및 AnalysisResult 객체로 매핑
            AnalysisResult result = chatClient.prompt()
                    .system(systemPrompt)
                    .user(logContent)
                    .call()
                    .entity(AnalysisResult.class);

            log.info("Cognitive distortion analysis completed - Distortions found: {}",
                    result.getDistortions() != null ? result.getDistortions().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("Failed to analyze cognitive distortions: {}", e.getMessage(), e);
            throw new RuntimeException("AI 분석 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateDynamicPrompt(Integer gravity, Integer stability) {
        log.info("Generating dynamic prompt - Gravity: {}, Stability: {}", gravity, stability);

        // System Prompt - Inner Orbit Mission Control 역할
        String systemPrompt = """
                You are 'Inner Orbit Mission Control'. Ask ONE powerful, thought-provoking question (max 2 sentences) based on the user's Gravity/Stability state.

                User's Current State:
                - Gravity (External Pressure): %d%%
                - Stability (Inner Strength): %d%%

                Guidelines:
                - If Gravity is high (>70): Ask about external pressures and coping mechanisms
                - If Stability is low (<30): Ask about self-care and grounding
                - If both are balanced (40-60 range): Ask about growth or reflection
                - Use compassionate, direct language
                - Keep it concise (max 2 sentences)
                - Write in Korean
                - Focus on empowerment, not judgment

                Return ONLY the question, without any prefix or explanation.
                """.formatted(gravity, stability);

        // User message variations (#32 - 다양성 증가)
        String[] userMessageVariations = {
                "Generate a thoughtful question for this user's current state.",
                "Create an insightful journaling prompt based on their emotional metrics.",
                "Craft a reflective question that helps them explore their current feelings.",
                "Ask a question that encourages self-awareness and emotional processing.",
                "Formulate a question that invites deeper introspection about their state."
        };

        // 랜덤하게 user message 선택
        String userMessage = userMessageVariations[(int) (Math.random() * userMessageVariations.length)];
        log.debug("Selected user message variation: {}", userMessage);

        try {
            // ChatClient를 사용하여 OpenAI API 호출
            // Temperature 0.9로 설정하여 더 창의적이고 다양한 질문 생성 (#33)
            ChatClient chatClient = chatClientBuilder
                    .defaultOptions(OpenAiChatOptions.builder()
                            .withTemperature(0.9)
                            .build())
                    .build();

            // 프롬프트 실행 및 문자열 결과 반환
            String prompt = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();

            log.info("Dynamic prompt generated successfully: {}", prompt);

            return prompt;

        } catch (Exception e) {
            log.error("Failed to generate dynamic prompt: {}", e.getMessage(), e);
            throw new RuntimeException("동적 프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
