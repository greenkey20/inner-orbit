/**
 * Advanced Analytics Utilities
 * 로그 데이터를 분석하여 패턴, 상관관계, 회복 탄력성을 계산하는 유틸리티 함수들
 */

/**
 * [HELPER] 한글 날짜 문자열("2025년 12월 9일 21:30")을 표준 Date 객체로 변환
 * Safari/iOS 호환성을 위해 필수
 */
function parseKoreanDate(dateStr) {
    if (!dateStr) return new Date(); // 방어 코드
    
    // 1. 이미 ISO 형식이면 바로 변환
    if (dateStr.includes('T') || dateStr.includes('-')) {
        return new Date(dateStr);
    }

    // 2. 한글 포맷 파싱 ("2025년 12월 9일 21:30")
    // 정규식으로 숫자만 추출: [2025, 12, 9, 21, 30]
    const parts = dateStr.match(/\d+/g);
    
    if (parts && parts.length >= 5) {
        // 월은 0부터 시작하므로 -1 해줘야 함
        return new Date(
            parts[0],     // Year
            parts[1] - 1, // Month (0-11)
            parts[2],     // Day
            parts[3],     // Hour
            parts[4]      // Minute
        );
    }
    
    return new Date(); // 파싱 실패 시 현재 시간
}

/**
 * 1. Correlation Analysis (상관관계 분석)
 * Gravity와 Stability 간의 피어슨 상관계수 계산 및 구간별 통계
 * * @param {Array} entries - 로그 엔트리 배열 [{id, gravity, stability, ...}]
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

    const numerator = (n * sumGS - sumG * sumS);
    const denominator = Math.sqrt((n * sumG2 - sumG * sumG) * (n * sumS2 - sumS * sumS));
    
    const correlation = denominator === 0 ? 0 : numerator / denominator;

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
 * * @param {Array} entries - 로그 엔트리 배열
 * @returns {Object} { dawn, morning, afternoon, night, maxPeriod }
 */
export function analyzeTemporalPatterns(entries) {
    if (!entries || entries.length === 0) {
        return { dawn: 0, morning: 0, afternoon: 0, night: 0, maxPeriod: null };
    }

    const patterns = { dawn: 0, morning: 0, afternoon: 0, night: 0 };

    entries.forEach(entry => {
        // parseKoreanDate 헬퍼 함수를 사용하여 안전하게 시간 추출
        const dateObj = parseKoreanDate(entry.date);
        const hour = dateObj.getHours();

        if (hour >= 0 && hour < 6) {
            patterns.dawn++;
        } else if (hour >= 6 && hour < 12) {
            patterns.morning++;
        } else if (hour >= 12 && hour < 18) {
            patterns.afternoon++;
        } else {
            patterns.night++;
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
 * * @param {Array} entries - 로그 엔트리 배열
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

    function extractUserContent(text) {
        const logSections = [];
        const lines = text.split('\n');
        let isInLogSection = false;
        let currentLogContent = [];

        for (const line of lines) {
            if (line.trim().startsWith('[Query]:')) {
                if (isInLogSection && currentLogContent.length > 0) {
                    logSections.push(currentLogContent.join(' '));
                    currentLogContent = [];
                }
                isInLogSection = false;
            } else if (line.trim().startsWith('[Log]:')) {
                isInLogSection = true;
                const content = line.replace(/^\[Log\]:\s*/, '').trim();
                if (content) {
                    currentLogContent.push(content);
                }
            } else if (isInLogSection) {
                const trimmed = line.trim();
                if (trimmed) {
                    currentLogContent.push(trimmed);
                }
            }
        }

        if (isInLogSection && currentLogContent.length > 0) {
            logSections.push(currentLogContent.join(' '));
        }

        return logSections.length > 0 ? logSections.join(' ') : text;
    }

    const allText = highGravityEntries
        .map(e => extractUserContent(e.content))
        .join(' ');

    const koreanWords = allText.match(/[가-힣]{2,}/g) || [];
    const englishWords = allText.match(/[a-zA-Z]{2,}/g) || [];
    const words = [...koreanWords, ...englishWords];

    const stopWords = new Set(['이것', '그것', '저것', '있다', '없다', '하다', '되다', '않다',
        '것이', '나는', '내가', '우리', '그리고', '하지만', '그런데',
        'the', 'is', 'am', 'are', 'and', 'or', 'but', 'in', 'on', 'at']);

    const frequency = {};
    words.forEach(word => {
        const normalized = word.toLowerCase();
        if (!stopWords.has(normalized)) {
            frequency[normalized] = (frequency[normalized] || 0) + 1;
        }
    });

    return Object.entries(frequency)
        .map(([word, count]) => ({ word, count }))
        .sort((a, b) => b.count - a.count)
        .slice(0, 20);
}

/**
 * 4. Resilience Index (회복 탄력성 지수)
 * Stability가 저점에서 회복(50% 이상)까지 걸리는 평균 시간 계산
 * * @param {Array} entries - 로그 엔트리 배열 (시간순 정렬 필요)
 * @returns {Object} { avgRecoveryDays, recoveryCount, trend }
 */
export function calculateResilienceIndex(entries) {
    if (!entries || entries.length < 2) {
        return { avgRecoveryDays: null, recoveryCount: 0, trend: null };
    }

    // [FIX 1] 정렬 기준 변경: 헬퍼 함수 사용
    const sorted = [...entries].sort((a, b) => parseKoreanDate(a.date) - parseKoreanDate(b.date));

    const recoveryPeriods = [];
    let lowPointTime = null;

    for (let i = 0; i < sorted.length; i++) {
        const entry = sorted[i];
        // [FIX 2] 시간 변환: 헬퍼 함수 사용
        const entryTime = parseKoreanDate(entry.date).getTime();

        // 저점 감지 (Stability <= 30)
        if (entry.stability <= 30 && lowPointTime === null) {
            lowPointTime = entryTime;
        }

        // 회복 감지 (Stability >= 50)
        if (lowPointTime !== null && entry.stability >= 50) {
            // [FIX 3] 계산 로직 변경: 시간 차이 계산
            const recoveryDays = (entryTime - lowPointTime) / (1000 * 60 * 60 * 24);
            
            // [안전장치] 유효한 값만 포함 (0.001일 이상)
            if (recoveryDays > 0.0001) {
                recoveryPeriods.push(recoveryDays);
            }
            
            lowPointTime = null;
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