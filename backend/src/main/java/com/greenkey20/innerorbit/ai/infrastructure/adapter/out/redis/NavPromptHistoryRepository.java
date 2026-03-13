package com.greenkey20.innerorbit.ai.infrastructure.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * 최근 생성된 항법 질문을 Redis List에 저장 — 반복 질문 방지용
 *
 * Key 구조: nav:recent:{userId}:{situation}
 * - situation별로 분리하여 같은 감정 상태에서의 질문만 exclusion 적용
 * 자료구조: Redis List (LPUSH + LTRIM으로 최근 N개만 유지)
 * TTL: 30일 / MAX_HISTORY: 100개 (3회/일 × 30일 기준)
 */
@Repository
@RequiredArgsConstructor
public class NavPromptHistoryRepository {

    private static final String KEY_PREFIX = "nav:recent:";
    private static final long MAX_HISTORY = 100;
    private static final Duration TTL = Duration.ofDays(30);

    private final StringRedisTemplate redisTemplate;

    public List<String> getRecentPrompts(Long userId, String situation) {
        String key = buildKey(userId, situation);
        List<String> prompts = redisTemplate.opsForList().range(key, 0, MAX_HISTORY - 1);
        return prompts != null ? prompts : Collections.emptyList();
    }

    public void savePrompt(Long userId, String situation, String prompt) {
        String key = buildKey(userId, situation);
        redisTemplate.opsForList().leftPush(key, prompt);
        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY - 1);
        redisTemplate.expire(key, TTL);
    }

    private String buildKey(Long userId, String situation) {
        return KEY_PREFIX + userId + ":" + situation;
    }
}
