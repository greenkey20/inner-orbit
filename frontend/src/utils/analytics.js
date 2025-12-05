/**
 * Advanced Analytics Utilities
 * 로그 데이터를 분석하여 패턴, 상관관계, 회복 탄력성을 계산하는 유틸리티 함수들
 */

/**
 * 1. Correlation Analysis (상관관계 분석)
 * Gravity와 Stability 간의 피어슨 상관계수 계산 및 구간별 통계
 * 
 * @param {Array} entries - 로그 엔트리 배열 [{id, gravity, stability, ...}]
 * @returns {Object} { correlation, highGravityAvgStability, veryHighGravityAvgStability }
 */
export function calculateCorrelation(entries) {
    if (!entries || entries.length < 2) {
        return { correlation: null, insights: [] };
    }

    const gravityValues = entries.map(e => e.gravity);
    const stabilityValues = entries.map(e => e.stability);

    const n = entries.length;
    const sumG = gravityValues.reduce((a, b) => a + b, 0);
    const sumS = stabilityValues.reduce((a, b) => a + b, 0);
    const sumGS = gravityValues.reduce((sum, g, i) => sum + g * stabilityValues[i], 0);
    const sumG2 = gravityValues.reduce((sum, g) => sum + g * g, 0);
    const sumS2 = stabilityValues.reduce((sum, s) => sum + s * s, 0);

    const correlation = (n * sumGS - sumG * sumS) /
        Math.sqrt((n * sumG2 - sumG * sumG) * (n * sumS2 - sumS * sumS));

    // 구간별 평균 계산
    const highGravity = entries.filter(e => e.gravity >= 60);
    const veryHighGravity = entries.filter(e => e.gravity >= 80);

    const highGravityAvgStability = highGravity.length > 0
        ? highGravity.reduce((sum, e) => sum + e.stability, 0) / highGravity.length
        : null;

    const veryHighGravityAvgStability = veryHighGravity.length > 0
        ? veryHighGravity.reduce((sum, e) => sum + e.stability, 0) / veryHighGravity.length
        : null;

    // 인사이트 생성
    const insights = [];
    if (veryHighGravityAvgStability !== null) {
        insights.push(
            `Gravity가 80% 이상일 때, 당신의 Stability는 평균 ${Math.round(veryHighGravityAvgStability)}%로 떨어집니다.`
        );
    }
    if (highGravityAvgStability !== null && veryHighGravityAvgStability === null) {
        insights.push(
            `Gravity가 60% 이상일 때, 당신의 Stability는 평균 ${Math.round(highGravityAvgStability)}%입니다.`
        );
    }
    if (correlation && !isNaN(correlation)) {
        const strength = Math.abs(correlation) > 0.7 ? '강한' : Math.abs(correlation) > 0.4 ? '중간' : '약한';
        const direction = correlation < 0 ? '음의' : '양의';
        insights.push(`Gravity와 Stability 간 ${strength} ${direction} 상관관계가 발견되었습니다. (r=${correlation.toFixed(2)})`);
    }

    return {
        correlation: isNaN(correlation) ? null : correlation,
        highGravityAvgStability,
        veryHighGravityAvgStability,
        insights
    };
}

/**
 * 2. Temporal Pattern Analysis (시간대 분석)
 * 시간대별 Gravity 경보 빈도 분석
 * 
 * @param {Array} entries - 로그 엔트리 배열
 * @returns {Object} { dawn, morning, afternoon, night, maxPeriod }
 */
export function analyzeTemporalPatterns(entries) {
    if (!entries || entries.length === 0) {
        return { dawn: 0, morning: 0, afternoon: 0, night: 0, maxPeriod: null };
    }

    const patterns = { dawn: 0, morning: 0, afternoon: 0, night: 0 };

    entries.forEach(entry => {
        // date 형식: "2024년 12월 5일 09:18" 등
        const dateStr = entry.date;
        const hourMatch = dateStr.match(/(\d{1,2}):(\d{2})/);

        if (hourMatch) {
            const hour = parseInt(hourMatch[1], 10);

            if (hour >= 0 && hour < 6) {
                patterns.dawn++;
            } else if (hour >= 6 && hour < 12) {
                patterns.morning++;
            } else if (hour >= 12 && hour < 18) {
                patterns.afternoon++;
            } else {
                patterns.night++;
            }
        }
    });

    // 가장 빈도가 높은 시간대 찾기
    const maxPeriod = Object.entries(patterns)
        .reduce((max, [period, count]) => count > max.count ? { period, count } : max, { period: null, count: 0 });

    return {
        ...patterns,
        maxPeriod: maxPeriod.period,
        maxCount: maxPeriod.count
    };
}

/**
 * 3. Keyword Extraction (키워드 추출)
 * 고위험 상태(High Gravity)일 때 사용된 단어 추출 및 빈도 계산
 * [Query]: 부분(프로그램 제공 질문)은 제외하고, [Log]: 부분(사용자 답변)만 분석
 * 
 * @param {Array} entries - 로그 엔트리 배열
 * @param {number} gravityThreshold - Gravity 임계값 (기본: 70)
 * @returns {Array} [{ word, count }] 빈도순 정렬
 */
export function extractKeywords(entries, gravityThreshold = 70) {
    if (!entries || entries.length === 0) {
        return [];
    }

    const highGravityEntries = entries.filter(e => e.gravity >= gravityThreshold);

    if (highGravityEntries.length === 0) {
        return [];
    }

    /**
     * [Query]: 부분을 제거하고 [Log]: 부분만 추출하는 헬퍼 함수
     * 예시:
     * "[Query]: 질문내용\n[Log]: 답변내용\n[Query]: 질문2\n[Log]: 답변2"
     * -> "답변내용 답변2"
     */
    function extractUserContent(text) {
        // [Log]: 로 시작하는 모든 섹션을 추출
        const logSections = [];
        const lines = text.split('\n');
        let isInLogSection = false;
        let currentLogContent = [];

        for (const line of lines) {
            if (line.trim().startsWith('[Query]:')) {
                // [Query]: 섹션 시작 - 이전 [Log] 섹션이 있으면 저장
                if (isInLogSection && currentLogContent.length > 0) {
                    logSections.push(currentLogContent.join(' '));
                    currentLogContent = [];
                }
                isInLogSection = false;
            } else if (line.trim().startsWith('[Log]:')) {
                // [Log]: 섹션 시작
                isInLogSection = true;
                // [Log]: 라벨 뒤의 내용도 포함
                const content = line.replace(/^\[Log\]:\s*/, '').trim();
                if (content) {
                    currentLogContent.push(content);
                }
            } else if (isInLogSection) {
                // [Log] 섹션 내용 계속
                const trimmed = line.trim();
                if (trimmed) {
                    currentLogContent.push(trimmed);
                }
            }
        }

        // 마지막 [Log] 섹션 저장
        if (isInLogSection && currentLogContent.length > 0) {
            logSections.push(currentLogContent.join(' '));
        }

        // [Log] 섹션이 없는 경우, 전체 텍스트를 사용 (기존 로그와의 호환성)
        return logSections.length > 0 ? logSections.join(' ') : text;
    }

    // 모든 텍스트 통합 (사용자 작성 부분만)
    const allText = highGravityEntries
        .map(e => extractUserContent(e.content))
        .join(' ');

    // 한글, 영문 단어 추출 (2글자 이상)
    const koreanWords = allText.match(/[가-힣]{2,}/g) || [];
    const englishWords = allText.match(/[a-zA-Z]{2,}/g) || [];
    const words = [...koreanWords, ...englishWords];

    // 불용어 제거
    const stopWords = new Set(['이것', '그것', '저것', '있다', '없다', '하다', '되다', '않다',
        '것이', '나는', '내가', '우리', '그리고', '하지만', '그런데',
        'the', 'is', 'am', 'are', 'and', 'or', 'but', 'in', 'on', 'at']);

    // 빈도 계산
    const frequency = {};
    words.forEach(word => {
        const normalized = word.toLowerCase();
        if (!stopWords.has(normalized)) {
            frequency[normalized] = (frequency[normalized] || 0) + 1;
        }
    });

    // 빈도순 정렬 (상위 20개)
    return Object.entries(frequency)
        .map(([word, count]) => ({ word, count }))
        .sort((a, b) => b.count - a.count)
        .slice(0, 20);
}

/**
 * 4. Resilience Index (회복 탄력성 지수)
 * Stability가 저점에서 회복(50% 이상)까지 걸리는 평균 시간 계산
 * 
 * @param {Array} entries - 로그 엔트리 배열 (시간순 정렬 필요)
 * @returns {Object} { avgRecoveryDays, recoveryCount, trend }
 */
export function calculateResilienceIndex(entries) {
    if (!entries || entries.length < 2) {
        return { avgRecoveryDays: null, recoveryCount: 0, trend: null };
    }

    // 날짜순으로 정렬 (id가 timestamp이므로 id순 정렬)
    const sorted = [...entries].sort((a, b) => a.id - b.id);

    const recoveryPeriods = [];
    let lowPointId = null;

    for (let i = 0; i < sorted.length; i++) {
        const entry = sorted[i];

        // 저점 감지 (Stability <= 30)
        if (entry.stability <= 30 && lowPointId === null) {
            lowPointId = entry.id;
        }

        // 회복 감지 (Stability >= 50)
        if (lowPointId !== null && entry.stability >= 50) {
            const recoveryDays = (entry.id - lowPointId) / (1000 * 60 * 60 * 24); // ms to days
            recoveryPeriods.push(recoveryDays);
            lowPointId = null;
        }
    }

    if (recoveryPeriods.length === 0) {
        return { avgRecoveryDays: null, recoveryCount: 0, trend: null };
    }

    const avgRecoveryDays = recoveryPeriods.reduce((a, b) => a + b, 0) / recoveryPeriods.length;

    // 트렌드 계산 (최근 절반 vs 이전 절반)
    let trend = null;
    if (recoveryPeriods.length >= 4) {
        const mid = Math.floor(recoveryPeriods.length / 2);
        const recentAvg = recoveryPeriods.slice(mid).reduce((a, b) => a + b, 0) / (recoveryPeriods.length - mid);
        const pastAvg = recoveryPeriods.slice(0, mid).reduce((a, b) => a + b, 0) / mid;
        const improvement = pastAvg - recentAvg;

        if (Math.abs(improvement) > 0.2) {
            trend = improvement > 0 ? 'improving' : 'declining';
        } else {
            trend = 'stable';
        }
    }

    return {
        avgRecoveryDays,
        recoveryCount: recoveryPeriods.length,
        trend
    };
}
