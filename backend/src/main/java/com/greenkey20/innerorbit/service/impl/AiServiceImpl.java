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

                    Example question styles (generate similar but different questions):
                    - "지금 버틸 수 있게 하는 건?" (strength mining, short)
                    - "작은 안전감을 느낄 수 있는 곳은?" (specific, somatic)
                    - "누구의 목소리가 필요한가요?" (relational, others)
                    - "이 순간을 견디려면 뭐가 필요해요?" (present, direct, action)
                    - "숨 쉴 공간은?" (ultra-short, metaphorical)
                    - "도움 요청할 수 있나요?" (binary-ish, direct)

                    CRITICAL:
                    - Keep it SHORT and DIRECT (crisis = simplicity)
                    - Focus on IMMEDIATE needs, not long-term reflection
                    - Vary time (present mostly, but sometimes "방금 전엔?", "다음 5분은?")
                    - Vary length (ultra-short to 1 sentence max)
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "OVERWHELMED" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (HIGH PRESSURE + MEDIUM STABILITY = OVERWHELMED)

                    Focus: Boundary-setting, pressure management, and what's helping them maintain resilience.

                    Example question styles:
                    - "어떤 경계가 필요한가요?" (value-based, direct)
                    - "아니오라고 말할 수 있는 것은?" (action, empowerment)
                    - "지금 압박 중 무엇을 내려놓을 수 있어요?" (present, specific action)
                    - "당신을 지탱하는 힘은 어디서 오나요?" (strength, open-ended)
                    - "압박 vs 휴식, 균형점은?" (binary, short)
                    - "이 상황에서 자신을 지키려면?" (self-protection, future-ish)

                    CRITICAL:
                    - Focus on BOUNDARIES and what they can CONTROL
                    - Mix present and near-future ("오늘", "이번 주")
                    - Vary between empowerment and self-compassion
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "RESILIENT_UNDER_PRESSURE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (HIGH PRESSURE + HIGH STABILITY = RESILIENT)

                    Focus: Strength sources, how they maintain balance, and successful coping patterns.

                    Example question styles:
                    - "이 힘은 어디서 오나요?" (strength mining, short)
                    - "압박 속에서도 중심을 잡는 비결은?" (pattern recognition, present)
                    - "어떻게 균형을 유지하고 있어요?" (coping, open-ended)
                    - "이 강인함을 어떻게 키워왔나요?" (past-reflective, growth)
                    - "다른 사람들과 나누고 싶은 통찰은?" (relational, world)
                    - "내일도 이렇게 버틸 수 있으려면?" (future, sustaining)

                    CRITICAL:
                    - Celebrate their STRENGTH while acknowledging pressure
                    - Mix past (how they built this) and future (sustaining)
                    - Can be slightly longer and more reflective
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "UNSTABLE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + LOW STABILITY = UNSTABLE)

                    Focus: Grounding techniques, emotional regulation, building inner security and self-compassion.

                    Example question styles:
                    - "발을 땅에 디딜 수 있게 하는 건?" (grounding, metaphorical)
                    - "감정의 파도를 탈 방법은?" (regulation, metaphorical, somatic)
                    - "자신에게 해줄 수 있는 따뜻한 말은?" (self-compassion, action)
                    - "몸이 안정을 느낄 때는 언제인가요?" (somatic, pattern)
                    - "내면의 흔들림, 무엇이 원인일까요?" (feeling-based, exploration)
                    - "안전하다고 느낀 순간은?" (past, sensation)

                    CRITICAL:
                    - Focus on GROUNDING and SAFETY
                    - Use somatic/body-based questions often
                    - Gentle, compassionate tone
                    - Mix present sensation and past安全 moments
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "BALANCED" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + MEDIUM STABILITY = BALANCED)

                    Focus: Growth opportunities, meaningful reflection, values, goals, and personal development.

                    Example question styles:
                    - "지금 성장하고 싶은 부분은?" (growth, future-oriented)
                    - "무엇이 당신에게 의미 있나요?" (value-based, open)
                    - "이 균형 속에서 발견한 건?" (present, reflective)
                    - "다음 도전은 무엇일까요?" (future, aspiration)
                    - "당신다운 삶이란?" (value, identity, deep)
                    - "최근 배운 것 중 가장 소중한 건?" (past, wisdom)

                    CRITICAL:
                    - Encourage EXPLORATION and GROWTH
                    - Balance past learning, present awareness, future aspiration
                    - Can be deeper and more philosophical
                    - Mix short and contemplative
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "GROWING" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (MEDIUM PRESSURE + HIGH STABILITY = GROWING)

                    Focus: Leveraging stability for new challenges, exploring potential and aspirations.

                    Example question styles:
                    - "이 안정감을 어디에 쓰고 싶어요?" (future, action, resource)
                    - "다음 모험은?" (ultra-short, future, excitement)
                    - "어떤 가능성이 보이나요?" (present, aspiration, open)
                    - "당신의 잠재력은 어디를 향하나요?" (future, potential, metaphorical)
                    - "도전하고 싶었던 것은?" (past-desire, action)
                    - "성장의 다음 단계는?" (future, development, direct)

                    CRITICAL:
                    - ENERGETIC and FORWARD-LOOKING tone
                    - Focus on POTENTIAL and POSSIBILITY
                    - Mix excitement and thoughtfulness
                    - Future-oriented mostly
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "SELF_DOUBT" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + LOW STABILITY = SELF-DOUBT)

                    Focus: Self-worth, internal narratives, reconnecting with strengths.

                    Example question styles:
                    - "당신의 가치는 무엇인가요?" (value, identity, direct)
                    - "스스로에게 하는 말은 진실인가요?" (thought-based, pattern)
                    - "자신에 대해 다시 쓴다면?" (narrative, past-present, metaphorical)
                    - "당신이 가진 강점을 기억하나요?" (strength, past, gentle)
                    - "내면의 비판, 누구의 목소리인가요?" (relational, pattern, deep)
                    - "자신을 믿을 수 있었던 순간은?" (past, strength, sensation)

                    CRITICAL:
                    - GENTLE and COMPASSIONATE tone
                    - Focus on RECONNECTING with worth and strength
                    - Challenge negative narratives gently
                    - Mix past strengths and present re-evaluation
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "REFLECTIVE" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + MEDIUM STABILITY = REFLECTIVE)

                    Focus: Life lessons, meaningful experiences, deep introspection, wisdom-gathering.

                    Example question styles:
                    - "삶이 가르쳐준 것은?" (past, wisdom, open)
                    - "최근 경험에서 발견한 의미는?" (past-present, meaning)
                    - "당신을 변화시킨 순간은?" (past, transformation, narrative)
                    - "지금 깨닫고 있는 진실은?" (present, insight, deep)
                    - "이 여정에서 얻은 지혜는?" (past, metaphorical, wisdom)
                    - "과거의 자신에게 전하고 싶은 말은?" (past-reflective, compassion)

                    CRITICAL:
                    - CONTEMPLATIVE and DEEP tone
                    - Focus on MEANING and WISDOM
                    - Can be longer and more poetic
                    - Mostly past-reflective and present-insight
                    - Write in Korean
                    - Return ONLY the question
                    """.formatted(gravity, stability);

            case "THRIVING" -> """
                    You are 'Inner Orbit Mission Control'.

                    User State: Gravity %d%%, Stability %d%% (LOW PRESSURE + HIGH STABILITY = THRIVING)

                    Focus: Joy, gratitude, life appreciation, sustaining and sharing this positive state.

                    Example question styles:
                    - "무엇이 당신을 빛나게 하나요?" (present, joy, metaphorical)
                    - "감사한 순간은?" (past, gratitude, short)
                    - "이 기쁨을 나누고 싶은 사람은?" (relational, sharing, present)
                    - "지금 느끼는 풍요로움은?" (present, appreciation, sensation)
                    - "내일도 웃을 수 있으려면?" (future, sustaining, gentle)
                    - "삶의 어떤 부분이 축복인가요?" (present, gratitude, value)

                    CRITICAL:
                    - WARM and CELEBRATORY tone
                    - Focus on JOY, GRATITUDE, APPRECIATION
                    - Mix celebrating present and sustaining future
                    - Can include sharing/relational aspects
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
}
