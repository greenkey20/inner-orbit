package com.greenkey20.innerorbit.domain.entity;

/**
 * 로그 엔트리의 타입을 정의하는 Enum
 * - DAILY: 일반 일상 로그 (기본값)
 * - SENSORY: 감각적 경험 기록 (여행 모드)
 * - INSIGHT: CS 개념 매핑 로그 (Architecture of Insight)
 */
public enum LogType {
    DAILY,
    SENSORY,
    INSIGHT
}
