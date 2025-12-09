/**
 * AI Service - Dynamic Prompting 서비스 (Backend API 연동)
 * 사용자의 Gravity/Stability 상태에 맞는 맞춤형 질문을 백엔드에서 생성합니다.
 *
 * ⚠️ 참고:
 * - 인지적 왜곡 분석: POST /api/logs/{id}/analyze
 * - 동적 프롬프트 생성: GET /api/ai/prompt
 * - OpenAI API Key는 백엔드에서 안전하게 관리됩니다.
 */

/**
 * API Key 가져오기 (하위 호환성 유지)
 * @returns {boolean} 항상 true 반환 (API Key는 백엔드에서 관리)
 */
export function getApiKey() {
    return true;
}

/**
 * Dynamic Prompt 생성 (백엔드 API 호출)
 * @param {number} gravity - 외부 인력 (0-100)
 * @param {number} stability - 코어 안정성 (0-100)
 * @returns {Promise<string>} - AI가 생성한 질문
 */
export async function generateDynamicPrompt(gravity, stability) {
    try {
        // 백엔드 API 호출
        const response = await fetch(`/api/ai/prompt?gravity=${gravity}&stability=${stability}`);

        if (!response.ok) {
            throw new Error(`서버 오류: ${response.status}`);
        }

        const data = await response.json();

        // 백엔드 응답 형식: {"prompt": "..."}
        if (!data.prompt) {
            throw new Error('유효하지 않은 응답 형식');
        }

        return data.prompt;

    } catch (error) {
        console.error('Backend API Error:', error);

        if (error.message.includes('Failed to fetch')) {
            throw new Error('백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.');
        } else {
            throw new Error(`신호 유실: ${error.message}`);
        }
    }
}
