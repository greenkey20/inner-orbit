package com.greenkey20.innerorbit.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenkey20.innerorbit.domain.dto.response.AnalysisResult;
import com.greenkey20.innerorbit.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 서비스 구현체
 * Spring AI의 ChatClient를 사용하여 OpenAI GPT 모델과 통신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

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

    /**
     * Gravity/Stability 값으로 사용자의 현재 상황을 9개 카테고리 중 하나로 판단
     */
    private String determineSituation(Integer gravity, Integer stability) {
        if (gravity > 67 && stability < 34) return "CRISIS";
        if (gravity > 67 && stability >= 34 && stability <= 66) return "OVERWHELMED";
        if (gravity > 67 && stability > 66) return "RESILIENT_UNDER_PRESSURE";
        if (gravity >= 34 && gravity <= 66 && stability < 34) return "UNSTABLE";
        if (gravity >= 34 && gravity <= 66 && stability >= 34 && stability <= 66) return "BALANCED";
        if (gravity >= 34 && gravity <= 66 && stability > 66) return "GROWING";
        if (gravity < 34 && stability < 34) return "SELF_DOUBT";
        if (gravity < 34 && stability >= 34 && stability <= 66) return "REFLECTIVE";
        if (gravity < 34 && stability > 66) return "THRIVING";
        return "BALANCED"; // fallback
    }

    /**
     * 상황별 맞춤 시스템 프롬프트 생성 (옵션 A: 구조화된 접근)
     */
    private String buildPromptForSituation(String situation, Integer gravity, Integer stability) {
        return switch(situation) {
            case "CRISIS" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (HIGH PRESSURE + LOW STABILITY = CRISIS)

                    Focus: Immediate coping strategies and finding small moments of relief or support.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "버틸 수 있어요?" (ultra-short)
                    - "도움 필요해?" (ultra-short, informal)
                    - "숨 쉴 공간은?" (ultra-short, metaphorical)
                    - "누가 도와줄 수 있어요?" (relational, direct)
                    - "안전한 곳은 어디예요?" (somatic, place)
                    - "5분만 쉴 수 있나요?" (time-specific, near-future)
                    - "방금 전엔 괜찮았어요?" (past, checking)
                    - "쉼 vs 도움?" (binary, ultra-short)

                    CRITICAL:
                    - NO "지금", "현재", "이 순간" - keep it ultra-short
                    - URGENT tone, not reflective
                    - Most questions should be 2-5 words
                    - Vary: ultra-short / informal / binary / time-specific
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "OVERWHELMED" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (HIGH PRESSURE + MEDIUM STABILITY = OVERWHELMED)

                    Focus: Boundary-setting, pressure management, and what's helping them maintain resilience.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "아니오라고 할 수 있는 건?" (boundary, direct)
                    - "내려놓을 수 있는 건?" (action, short)
                    - "압박 vs 휴식?" (binary, ultra-short)
                    - "어떤 경계가 필요해요?" (boundary, direct)
                    - "오늘 무엇을 거절할 수 있어요?" (future, action)
                    - "당신을 버티게 하는 건?" (strength, short)
                    - "이번 주, 무엇을 줄일까요?" (near-future, specific)
                    - "스스로를 지키려면?" (self-protection, action)

                    CRITICAL:
                    - AVOID "지금", "현재" - use "오늘", "이번 주" instead
                    - Focus on ACTION and CONTROL
                    - Keep questions SHORT (3-7 words)
                    - Mix binary / action / boundary setting
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "RESILIENT_UNDER_PRESSURE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (HIGH PRESSURE + HIGH STABILITY = RESILIENT)

                    Focus: Strength sources, how they maintain balance, and successful coping patterns.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "힘의 원천은?" (ultra-short, strength)
                    - "어떻게 버텨왔어요?" (past, coping)
                    - "균형의 비결은?" (short, pattern)
                    - "내일도 버틸 수 있으려면?" (future, sustaining)
                    - "이 강인함, 어디서 배웠어요?" (past, growth)
                    - "나누고 싶은 지혜는?" (relational, short)
                    - "중심을 잡게 하는 건?" (strength, direct)
                    - "압박 속 평온, 비결은?" (paradox, short)

                    CRITICAL:
                    - CELEBRATE strength while acknowledging pressure
                    - AVOID "지금" - use past ("어떻게~왔어요") or future ("~려면")
                    - Mix SHORT questions and slightly longer reflective ones
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "UNSTABLE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + LOW STABILITY = UNSTABLE)

                    Focus: Grounding techniques, emotional regulation, building inner security and self-compassion.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "발바닥 느껴져요?" (somatic, ultra-short)
                    - "숨은 어떻게 흐르나요?" (somatic, breathing)
                    - "몸 어디가 편안해요?" (somatic, safe spot)
                    - "안전했던 순간은?" (past, safe memory)
                    - "자신에게 해줄 말은?" (self-compassion, short)
                    - "감정의 파도, 어디쯤 왔어요?" (metaphorical, present)
                    - "땅에 닿는 느낌은?" (grounding, somatic)
                    - "안정 vs 흔들림?" (binary, sensation)

                    CRITICAL:
                    - AVOID "지금" - use somatic present ("느껴져요?", "흐르나요?")
                    - Focus on BODY sensations, not "감정은 무엇"
                    - Keep it GENTLE and SHORT
                    - Mix somatic / past-safe / metaphorical
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "BALANCED" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + MEDIUM STABILITY = BALANCED)

                    Focus: Growth opportunities, meaningful reflection, values, goals, and personal development.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "다음 도전은?" (ultra-short, future)
                    - "무엇이 의미 있어요?" (value, short)
                    - "성장하고 싶은 부분은?" (growth, future)
                    - "당신다운 삶이란?" (identity, deep)
                    - "최근 배운 건?" (past, wisdom, short)
                    - "균형 속 발견은?" (present, reflective, short)
                    - "어떤 가치가 빛나요?" (value, metaphorical)
                    - "내일의 나는?" (future, identity, short)

                    CRITICAL:
                    - AVOID "지금" - use past/future balance
                    - Mix GROWTH (future) and WISDOM (past)
                    - Vary length: ultra-short to philosophical
                    - Focus on VALUES and MEANING
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "GROWING" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + HIGH STABILITY = GROWING)

                    Focus: Leveraging stability for new challenges, exploring potential and aspirations.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "다음 모험은?" (ultra-short, future)
                    - "어떤 가능성이 보여요?" (potential, open)
                    - "도전하고 싶은 건?" (action, future, short)
                    - "안정감을 어디에 쓸까요?" (resource, future)
                    - "잠재력의 방향은?" (potential, short)
                    - "성장의 다음 단계는?" (future, development)
                    - "뭘 시작해볼까요?" (action, ultra-short, future)
                    - "꿈꾸던 도전은?" (past-desire, future-action)

                    CRITICAL:
                    - ENERGETIC and FORWARD-LOOKING
                    - ALL questions should be FUTURE-oriented
                    - Keep it SHORT and EXCITING
                    - Focus on ACTION and POSSIBILITY
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "SELF_DOUBT" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + LOW STABILITY = SELF-DOUBT)

                    Focus: Self-worth, internal narratives, reconnecting with strengths.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "당신의 가치는?" (ultra-short, worth)
                    - "강점을 기억하나요?" (strength, past, gentle)
                    - "스스로에게 하는 말, 진실인가요?" (narrative, challenge)
                    - "믿을 수 있었던 순간은?" (past, strength, memory)
                    - "자신에 대해 다시 쓴다면?" (narrative, metaphorical)
                    - "내면의 비판, 누구 목소리예요?" (relational, pattern)
                    - "당신이 빛났던 때는?" (past, strength, metaphorical)
                    - "자신을 어떻게 보고 싶어요?" (future, re-framing)

                    CRITICAL:
                    - GENTLE and COMPASSIONATE
                    - Focus on PAST STRENGTHS and RE-FRAMING
                    - AVOID "지금" - use past ("~했던", "~기억하나요")
                    - Keep it SHORT and WARM
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "REFLECTIVE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + MEDIUM STABILITY = REFLECTIVE)

                    Focus: Life lessons, meaningful experiences, deep introspection, wisdom-gathering.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "삶이 가르쳐준 건?" (past, wisdom, short)
                    - "변화시킨 순간은?" (past, transformation)
                    - "최근 발견한 의미는?" (past-present, meaning)
                    - "여정에서 얻은 지혜는?" (past, metaphorical)
                    - "과거의 나에게 전할 말은?" (past-reflective, compassion)
                    - "어떤 진실이 보이나요?" (present, insight)
                    - "깨달은 건?" (ultra-short, wisdom)
                    - "경험이 남긴 건?" (past, legacy)

                    CRITICAL:
                    - CONTEMPLATIVE and DEEP
                    - Focus on PAST lessons and PRESENT insights
                    - AVOID "지금" - use "최근", "여정", past tense
                    - Can be SHORT or POETIC
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "THRIVING" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + HIGH STABILITY = THRIVING)

                    Focus: Joy, gratitude, life appreciation, sustaining and sharing this positive state.

                    Example question styles (generate similar but DIFFERENT questions):
                    - "빛나게 하는 건?" (ultra-short, joy)
                    - "감사한 순간은?" (past, gratitude, short)
                    - "기쁨을 나눌 사람은?" (relational, sharing)
                    - "축복은?" (ultra-short, gratitude)
                    - "내일도 웃으려면?" (future, sustaining)
                    - "풍요로움은 어디서?" (appreciation, source)
                    - "삶의 선물은?" (gratitude, metaphorical, short)
                    - "이 기쁨, 어떻게 지킬까요?" (future, sustaining)

                    CRITICAL:
                    - WARM and CELEBRATORY
                    - Focus on JOY and GRATITUDE
                    - AVOID "지금 느끼는" - use "빛나게 하는", "감사한", "축복"
                    - Keep it SHORT and WARM
                    - Mix present celebration and future sustaining
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            default -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%%

                    Generate a thoughtful, diverse question in Korean that helps the user explore their current emotional state.
                    Vary your style: use different time perspectives (past/present/future), lengths (short/long), and types (open/action/metaphorical).

                    Return ONLY the question.
                    """.formatted(gravity, stability);
        };
    }

    @Override
    public String generateDynamicPrompt(Integer gravity, Integer stability) {
        // 1단계: 상황 판단
        String situation = determineSituation(gravity, stability);
        log.info("Generating dynamic prompt - Gravity: {}, Stability: {} -> Situation: {}",
                gravity, stability, situation);

        // 2단계: 상황별 맞춤 시스템 프롬프트 생성
        String systemPrompt = buildPromptForSituation(situation, gravity, stability);

        // User message 단순화 (옵션 1) - 상황별 프롬프트가 이미 충분히 구체적이므로 단순하게
        String userMessage = "Generate one question.";

        try {
            // ChatClient를 사용하여 OpenAI API 호출
            // Temperature 0.9로 설정하여 더 창의적이고 다양한 질문 생성 (#33)
            ChatClient chatClient = chatClientBuilder
                    .defaultOptions(OpenAiChatOptions.builder()
                            .temperature(0.9)
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

    @Override
    public List<String> suggestCsKeywords(String trigger) {
        log.info("Starting CS keyword suggestion for trigger: {}", trigger.substring(0, Math.min(50, trigger.length())));

        String systemPrompt = """
                You are a creative Computer Science educator helping developers see CS concepts in everyday life.

                Your task: Analyze a real-world observation and suggest 3-5 related Computer Science or Software Engineering concepts.

                Guidelines:
                - Be creative but not too far-fetched
                - Mix fundamental concepts (algorithms, data structures) with systems concepts (networking, databases, architecture)
                - Include both technical terms and conceptual patterns
                - Prefer concepts that have clear real-world parallels
                - Return concepts in both Korean and English if helpful

                Examples:

                Observation: "오토바이들이 신호등 앞에서 물결처럼 밀려왔다가 신호가 바뀌면 한꺼번에 빠져나간다"
                Suggestions: ["Message Queue (메시지 큐)", "Async Processing (비동기 처리)", "Rate Limiting (처리율 제한)", "Load Balancing (부하 분산)", "Event Stream (이벤트 스트림)"]

                Observation: "도서관에서 책을 찾다가 색인을 보니 훨씬 빨랐다"
                Suggestions: ["Hash Table (해시 테이블)", "Binary Search (이진 탐색)", "Indexing (인덱싱)", "B-Tree", "Cache (캐시)"]

                CRITICAL: Return ONLY a valid JSON array of strings, nothing else.
                Format: ["Concept 1", "Concept 2", "Concept 3", "Concept 4", "Concept 5"]

                Keep each concept concise (1-3 words in English, Korean translation optional in parentheses).
                """;

        try {
            ChatClient chatClient = chatClientBuilder
                    .defaultOptions(OpenAiChatOptions.builder()
                            .temperature(0.8) // Creative but not too random
                            .build())
                    .build();

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user("관찰: " + trigger)
                    .call()
                    .content();

            log.debug("Raw AI response: {}", response);

            // JSON 배열 파싱
            List<String> keywords = objectMapper.readValue(response, new TypeReference<List<String>>() {});

            // 3-5개로 제한
            if (keywords.size() > 5) {
                keywords = keywords.subList(0, 5);
            }

            log.info("CS keyword suggestion completed - {} keywords generated", keywords.size());

            return keywords;

        } catch (Exception e) {
            log.error("Failed to suggest CS keywords: {}", e.getMessage(), e);
            // Fallback: 기본 키워드 반환
            return List.of("Algorithm (알고리즘)", "Data Structure (자료구조)", "Pattern (패턴)");
        }
    }

    @Override
    public String generateInsightFeedback(String trigger, String abstraction, String application) {
        log.info("Generating insight feedback - Abstraction: {}", abstraction);

        String systemPrompt = """
                You are a supportive mentor helping a developer build "Architecture of Insight" -
                the skill of seeing Computer Science patterns in everyday life.

                Your role:
                - Review their insight mapping (Observation → CS Concept → Application)
                - Provide encouraging, constructive feedback
                - Validate creative connections
                - Suggest additional related concepts or deeper insights
                - Help them refine their thinking

                Structure your feedback in Korean with 3 parts:
                1. 격려 (Encouragement): What's good about this insight? (1-2 sentences)
                2. 심화 (Deepening): How can this connection be explored further? (2-3 sentences)
                3. 확장 (Extension): What other CS concepts could relate? (1-2 sentences, suggest 1-2 more concepts)

                Keep it warm, constructive, and intellectually stimulating.
                Total length: 4-6 sentences.
                Write in Korean.
                """;

        String userMessage = String.format("""
                사용자의 Insight Log:

                [관찰 (Trigger)]
                %s

                [CS 개념 (Abstraction)]
                %s

                [적용점 (Application)]
                %s

                이 통찰에 대해 피드백해주세요.
                """, trigger, abstraction, application);

        try {
            ChatClient chatClient = chatClientBuilder
                    .defaultOptions(OpenAiChatOptions.builder()
                            .temperature(0.7)
                            .build())
                    .build();

            String feedback = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();

            log.info("Insight feedback generated successfully");

            return feedback;

        } catch (Exception e) {
            log.error("Failed to generate insight feedback: {}", e.getMessage(), e);
            return "통찰을 기록해주셔서 감사합니다. 일상에서 CS 개념을 발견하는 것은 개발자적 사고를 키우는 훌륭한 연습입니다.";
        }
    }
}
