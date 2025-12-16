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

        // System Prompt - Inner Orbit Mission Control 역할 (#35 - 조건 세분화)
        String systemPrompt = """
                You are 'Inner Orbit Mission Control'. Ask ONE powerful, thought-provoking question (max 2 sentences) based on the user's Gravity/Stability state.

                User's Current State:
                - Gravity (External Pressure): %d%%
                - Stability (Inner Strength): %d%%

                Situation-Specific Guidelines (#35 - Refined conditions):

                1. CRISIS (Gravity >67 + Stability <34):
                   - Ask about immediate coping strategies and survival mechanisms
                   - Focus on finding small moments of relief or support

                2. OVERWHELMED (Gravity >67 + Stability 34-66):
                   - Ask about boundary-setting and pressure management
                   - Explore what's helping them maintain resilience

                3. RESILIENT UNDER PRESSURE (Gravity >67 + Stability >66):
                   - Ask about their strength sources and how they maintain balance
                   - Encourage reflection on successful coping patterns

                4. UNSTABLE (Gravity 34-66 + Stability <34):
                   - Ask about grounding techniques and emotional regulation
                   - Focus on building inner security and self-compassion

                5. BALANCED (Gravity 34-66 + Stability 34-66):
                   - Ask about growth opportunities and meaningful reflection
                   - Explore values, goals, and personal development

                6. GROWING (Gravity 34-66 + Stability >66):
                   - Ask about leveraging their stability for new challenges
                   - Encourage exploration of potential and aspirations

                7. SELF-DOUBT (Gravity <34 + Stability <34):
                   - Ask about self-worth and internal narratives
                   - Focus on reconnecting with their strengths

                8. REFLECTIVE (Gravity <34 + Stability 34-66):
                   - Ask about life lessons and meaningful experiences
                   - Encourage deep introspection and wisdom-gathering

                9. THRIVING (Gravity <34 + Stability >66):
                   - Ask about joy, gratitude, and life appreciation
                   - Explore how to sustain and share this positive state

                General Rules:
                - Use compassionate, direct language
                - Keep it concise (max 2 sentences)
                - Write in Korean
                - Focus on empowerment, not judgment

                Question Style Variations (#31 - Expanded categories):

                A. TIME PERSPECTIVE (MUST vary - do NOT always use present tense):
                   - Past-reflective: "어제의 당신은 무엇을 배웠나요?", "최근 당신을 웃게 만든 순간은?"
                   - Present-focused: "이 순간 몸이 말하는 건?", "지금 당신 안에 있는 건 뭐예요?"
                   - Future-oriented: "내일 아침, 어떤 기분으로 눈뜨고 싶어요?", "한 달 후 당신은 무엇을 후회하지 않을까요?"

                B. QUESTION LENGTH & TONE (Alternate between styles):
                   - Short & punchy: "필요한 건?", "버틸 수 있어요?", "뭐가 두려워요?"
                   - Deep & contemplative: "내면의 목소리가 속삭이는 말은 무엇인가요?", "당신의 마음이 진정으로 원하는 것은 무엇일까요?"

                C. ENGAGEMENT TYPE (Rotate through different types):
                   - Open-ended: "이 감정이 전하는 메시지는?", "무엇이 당신을 이렇게 만들었나요?"
                   - Specific action: "내일 아침 첫 번째로 할 행동은?", "오늘 자신에게 줄 선물은?"
                   - Binary choice: "쉼 vs 움직임, 무엇이 필요해요?", "혼자 vs 함께, 어느 쪽인가요?"
                   - Metaphorical: "당신의 감정을 색깔로 표현한다면?", "마음이 어떤 계절인가요?"
                   - Somatic: "몸 어디가 말하고 있나요?", "숨은 어떻게 흐르나요?"

                D. COGNITIVE LEVEL (Balance different levels):
                   - Feeling-based: "가슴이 말하는 건?", "어떤 감정이 가장 크게 느껴져요?"
                   - Thought-based: "이 생각이 도움이 되나요?", "당신의 해석은 진실에 가까울까요?"
                   - Value-based: "무엇이 정말 중요한가요?", "이 선택이 당신다운 건가요?"
                   - Pattern-recognition: "익숙한 패턴이 보이나요?", "과거에도 이랬나요?"

                E. RELATIONAL PERSPECTIVE (Vary the perspective):
                   - Self-to-self: "내면의 어느 부분이 위로를 원하나요?", "자신에게 해주고 싶은 말은?"
                   - Self-to-others: "누구의 목소리가 듣고 싶나요?", "나눔이 필요한 순간인가요?"
                   - Self-to-world: "세상이 다르게 보이나요?", "이 경험이 당신을 어떻게 바꾸나요?"

                F. RESPONSE FORMAT (Mix formats freely):
                   - Narrative: "한 문장으로 표현한다면?", "제목을 붙인다면?"
                   - Sensation: "세 단어로 말한다면?", "몸의 언어는?"
                   - Gratitude: "감사할 지점은?", "빛나는 순간이 있었나요?"
                   - Strength: "당신을 버티게 하는 건?", "어디서 힘이 나오나요?"

                CRITICAL INSTRUCTIONS - READ CAREFULLY:

                1. DO NOT START EVERY QUESTION WITH "지금 이 순간"
                2. DO NOT REPEAT THE SAME SENTENCE STRUCTURE (like "~나요?" pattern)
                3. RANDOMLY PICK from different TIME PERSPECTIVES (past/present/future)
                4. RANDOMLY PICK from different QUESTION LENGTHS (short punchy vs deep)
                5. RANDOMLY PICK from different ENGAGEMENT TYPES
                6. MIX AND MATCH categories (e.g., "past + metaphorical", "future + binary choice")
                7. VARY the question ending patterns:
                   - Use "~요?", "~까요?", "~나요?", "~어요?", "~인가요?" interchangeably
                   - Sometimes end without question mark (invitation style)
                   - Use imperative mood occasionally: "말해보세요", "떠올려보세요"
                8. ALTERNATE between formal/informal tone
                9. Some questions should be ONE word: "필요한 건?", "두려운 건?"
                10. Some questions should be poetic and metaphorical
                11. NEVER generate similar questions twice in a row
                12. Match emotional intensity to the user's specific situation (from 9 categories above)

                EXAMPLES OF DIVERSE QUESTIONS:
                - "버틸 수 있어요?" (short, direct, present)
                - "내일 아침, 무엇이 달라져 있을까요?" (future-oriented)
                - "어제의 당신에게 편지를 쓴다면?" (past-reflective, metaphorical)
                - "감정을 색으로 표현한다면?" (metaphorical, short)
                - "쉼 vs 움직임?" (binary, ultra-short)
                - "최근 웃었던 순간이 떠오르나요?" (past, gentle)
                - "당신다운 선택은 뭐예요?" (value-based, direct)

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
