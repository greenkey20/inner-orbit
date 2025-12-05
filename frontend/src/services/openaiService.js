import OpenAI from 'openai';

/**
 * OpenAI Service - Dynamic Prompting 서비스
 * 사용자의 Gravity/Stability 상태에 맞는 맞춤형 질문을 생성합니다.
 *
 * ⚠️ 참고: 인지적 왜곡 분석(analyzeCognitiveDistortions)은 백엔드로 이관되었습니다.
 * 백엔드 API 엔드포인트: POST /api/logs/{id}/analyze
 */

/**
 * API Key 가져오기 (localStorage에서)
 */
export function getApiKey() {
    return localStorage.getItem('openai_api_key');
}

/**
 * API Key 저장하기 (localStorage에)
 */
export function setApiKey(key) {
    localStorage.setItem('openai_api_key', key);
}

/**
 * API Key 삭제하기
 */
export function clearApiKey() {
    localStorage.removeItem('openai_api_key');
}

/**
 * Dynamic Prompt 생성
 * @param {number} gravity - 외부 인력 (0-100)
 * @param {number} stability - 코어 안정성 (0-100)
 * @returns {Promise<string>} - AI가 생성한 질문
 */
export async function generateDynamicPrompt(gravity, stability) {
    const apiKey = getApiKey();

    if (!apiKey) {
        throw new Error('API Key가 설정되지 않았습니다. Settings에서 API Key를 입력해주세요.');
    }

    const openai = new OpenAI({
        apiKey: apiKey,
        dangerouslyAllowBrowser: true // Client-side 사용을 위한 설정
    });

    // 상태 분석
    const state = analyzeState(gravity, stability);

    // System Prompt
    const systemPrompt = `You are "Inner Orbit Mission Control", an empathetic AI assistant helping users navigate their emotional states through journaling.

Your role is to ask ONE powerful, thought-provoking question (max 2 sentences) that helps the user reflect on their current state.

Current State Context:
- Gravity (External Pull): ${gravity}% - ${state.gravityLevel}
- Stability (Core Integrity): ${stability}% - ${state.stabilityLevel}
- Overall Condition: ${state.condition}

Guidelines:
- Be empathetic but not patronizing
- Focus on actionable reflection
- Avoid clichés
- Use metaphors related to space/flight when appropriate
- Keep it concise (max 2 sentences)
- Write in Korean`;

    try {
        const response = await openai.chat.completions.create({
            model: 'gpt-4o-mini',
            messages: [
                { role: 'system', content: systemPrompt },
                { role: 'user', content: `현재 상태: Gravity ${gravity}%, Stability ${stability}%. 나에게 필요한 질문을 해주세요.` }
            ],
            temperature: 0.8,
            max_tokens: 100
        });

        return response.choices[0].message.content.trim();
    } catch (error) {
        console.error('OpenAI API Error:', error);

        if (error.status === 401) {
            throw new Error('API Key가 유효하지 않습니다. Settings에서 확인해주세요.');
        } else if (error.status === 429) {
            throw new Error('API 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.');
        } else {
            throw new Error(`신호 유실: ${error.message}`);
        }
    }
}

/**
 * 상태 분석 헬퍼 함수
 */
function analyzeState(gravity, stability) {
    const gravityLevel = gravity > 70 ? '높음 (High)' : gravity > 30 ? '보통 (Medium)' : '낮음 (Low)';
    const stabilityLevel = stability > 70 ? '높음 (High)' : stability > 30 ? '보통 (Medium)' : '낮음 (Low)';

    let condition = '';
    if (gravity > 70 && stability < 30) {
        condition = '위기 상황: 외부 압박이 크고 내면이 흔들림';
    } else if (gravity < 30 && stability > 70) {
        condition = '안정 상태: 평온하고 단단함';
    } else if (gravity > 70 && stability > 70) {
        condition = '긴장된 균형: 압박 속에서도 버티고 있음';
    } else if (gravity < 30 && stability < 30) {
        condition = '공허함: 외부 자극도 없고 내적 동력도 약함';
    } else {
        condition = '과도기: 변화의 시기';
    }

    return { gravityLevel, stabilityLevel, condition };
}
