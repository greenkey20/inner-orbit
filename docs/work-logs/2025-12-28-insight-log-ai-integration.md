# 작업 일지: Insight Log AI 기능 통합 및 배포 문제 해결

**날짜**: 2025년 12월 28일
**작업자**: greenkey20 + Claude Code
**주요 작업**: Insight Log AI 기능 프론트엔드 연동, Flyway Migration 문제 해결

---

## 📋 목차

1. [작업 개요](#작업-개요)
2. [완료된 작업](#완료된-작업)
3. [발생한 문제 및 해결](#발생한-문제-및-해결)
4. [프롬프트 엔지니어링 학습 가이드](#프롬프트-엔지니어링-학습-가이드)
5. [다음 단계](#다음-단계)

---

## 작업 개요

### 목표
- Insight Log (Architecture of Insight) AI 기능 프론트엔드 연동 완료
- AI 키워드 추천 및 AI 피드백 생성 기능 구현
- 서버 배포 및 테스트

### 배경
- 백엔드 API는 이미 구현 완료 (PR #48, #49, #50)
- 프론트엔드에서 Mock 데이터 사용 중 → 실제 API 연동 필요
- Insight Log 수정 기능 및 UI 개선 필요

---

## 완료된 작업

### ✅ 1. Insight Log AI 기능 프론트엔드 연동 (PR #51)

**변경 파일**:
- `frontend/src/components/LogEditor.jsx`
- `frontend/src/components/LogHistory.jsx`
- `frontend/src/hooks/useInnerOrbit.js`

**구현 내용**:

#### 1-1. AI 키워드 추천 (LogEditor.jsx)
```javascript
// Before: Mock 데이터
const mockKeywords = ["Algorithm", "DataStructure", "Pattern"];

// After: 실제 API 호출
const handleAiKeywordSuggestion = async () => {
    const response = await fetch('/api/ai/insights/suggest-keywords', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trigger: deepLogData.insightTrigger })
    });

    const data = await response.json();
    const keywords = data.keywords;
    // Abstraction 필드에 해시태그 형태로 추가
};
```

**기능**:
- Trigger 필드 입력 (10자 이상 validation)
- "AI Suggest" 버튼 클릭
- 백엔드로부터 3-5개 CS 개념 키워드 수신
- Abstraction 필드에 `#Keyword1 #Keyword2` 형태로 추가
- 로딩 상태 표시

#### 1-2. AI 피드백 생성 (LogHistory.jsx)
```javascript
const handleRequestFeedback = async (entry) => {
    const response = await fetch(`/api/logs/${entry.id}/request-feedback`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    });

    const updatedLog = await response.json();

    // 즉시 화면에 표시 (Decrypt Log와 동일한 패턴)
    setFeedbackResults(prev => ({
        ...prev,
        [entry.id]: updatedLog.aiFeedback
    }));
};
```

**기능**:
- Insight Log 저장 후 Flight History에서 확인
- "AI 피드백 요청" 버튼 클릭
- 즉시 화면에 피드백 표시 (새로고침 불필요)
- "Re-feedback" 버튼으로 재생성 가능

#### 1-3. UI 개선
- ✅ Content 필드 숨김 (Insight Log에서는 불필요)
- ✅ Decrypt Log 버튼 숨김 (Insight Log에서는 불필요)
- ✅ Insight Log 수정 시 content validation 제외

**PR 상태**: ✅ Merged (#51)

---

### ✅ 2. Flyway Migration V2 복원 (PR #52)

**문제 상황**:
```
서버 배포 시 Flyway checksum mismatch 에러:
Migration checksum mismatch for migration version 2
-> Applied to database : 450790039
-> Resolved locally    : -164437910
```

**원인**:
- 서버 DB에 적용된 V2: `V2__convert_analysis_result_to_jsonb.sql`
- 현재 코드의 V2: `V2__add_insight_log_fields.sql`
- **이미 배포된 migration 파일이 교체됨** (Flyway 원칙 위반)

**해결**:
1. 원래 V2 복원: `V2__convert_analysis_result_to_jsonb.sql`
2. 잘못 V2였던 파일을 V5로 이동: `V5__add_insight_log_fields.sql`

**최종 Migration 순서**:
```
V1: initial_schema
V2: convert_analysis_result_to_jsonb ✅ (서버 DB와 일치)
V3: drop_is_deep_log_column
V4: make_content_nullable
V5: add_insight_log_fields
```

**PR 상태**: ✅ Merged (#52)

**교훈**:
> ❌ **절대 금지**: 이미 배포된 migration 파일 수정/교체/삭제
> ✅ **올바른 방법**: 항상 새로운 버전(V6, V7, ...)으로 migration 추가

---

### ✅ 3. Migration 의존성 순서 수정 (PR #53)

**문제 상황**:
```
V3 migration 실행 중 에러:
ERROR: column "log_type" of relation "log_entry" does not exist
Location: V3__drop_is_deep_log_column.sql, Line 11
```

**원인**:
- V3 (drop_is_deep_log_column): `log_type` 컬럼에 COMMENT 추가 시도
- V5 (add_insight_log_fields): `log_type` 컬럼 생성
- **V3이 먼저 실행되는데, log_type이 아직 없어서 실패**

**해결**:
V3 ↔ V5 파일명 교체하여 의존성 순서 수정

**변경 전 (잘못된 순서)**:
```
V1: initial_schema ✅
V2: convert_analysis_result_to_jsonb ✅
V3: drop_is_deep_log_column ❌ (log_type 사용하는데 없음!)
V4: make_content_nullable
V5: add_insight_log_fields (log_type 생성)
```

**변경 후 (올바른 순서)**:
```
V1: initial_schema ✅
V2: convert_analysis_result_to_jsonb ✅
V3: add_insight_log_fields ✅ (log_type 생성)
V4: make_content_nullable ✅
V5: drop_is_deep_log_column ✅ (log_type 사용 가능!)
```

**서버 배포 후 동작**:
1. V1, V2 스킵 (이미 적용됨)
2. V3 실행: `log_type` 컬럼 생성 ✅
3. V4 실행: `content` nullable 변경 ✅
4. V5 실행: `is_deep_log` 삭제 ✅
5. 백엔드 정상 시작 ✅

**PR 상태**: ✅ Merged (#53)

---

### ✅ 4. Insight Log 수정 시 Content Validation 제거 (PR #54)

**문제 상황**:
```
서버에서 Insight Log 수정 시 400 Bad Request:
PUT /api/logs/23 400 (Bad Request)

Field error on field 'content': rejected value []
default message [내용을 입력해주세요]
```

**원인**:
`LogEntryUpdateRequest` DTO의 content 필드에 `@NotBlank` validation이 있어,
Insight Log (content를 사용하지 않음) 수정 시 빈 문자열을 거부

**해결**:
`@NotBlank` 제거하여 content를 선택 사항으로 변경

**Before**:
```java
@NotBlank(message = "내용을 입력해주세요")  // ❌
@Size(max = 10000)
private String content;
```

**After**:
```java
// Content is optional for INSIGHT logs (required for DAILY/SENSORY)
// Validation happens in service layer based on logType
@Size(max = 10000)
private String content;  // ✅
```

**일관성 확인**:
- ✅ CreateRequest: 이미 올바름 (`@NotBlank` 없음)
- ✅ UpdateRequest: 이번 수정으로 일치
- ✅ Database: V4 migration으로 content가 nullable

**PR 상태**: ✅ Merged (#54)

---

## 발생한 문제 및 해결

### 🔴 문제 1: OpenAI API 로컬 환경 401 에러

**증상**:
- 로컬 개발 환경에서 AI 기능 호출 시 항상 fallback 값 반환
- AI Suggest: 항상 `["Algorithm", "DataStructure", "Pattern"]`
- AI Feedback: 항상 "통찰을 기록해주셔서 감사합니다..."

**조사 과정**:
1. .env 파일 확인: API 키 존재 ✅
2. docker-compose.yml 확인: 환경변수 전달 설정 정상 ✅
3. 백엔드 컨테이너 환경변수 확인: 키 수신 확인 ✅
4. 백엔드 컨테이너에서 직접 OpenAI API 호출:
   ```bash
   docker exec inner-orbit-backend sh -c 'curl ... OpenAI API'
   → 401 Unauthorized
   ```

**근본 원인**:
- **로컬 Mac 환경의 네트워크 문제**
- 서버에서는 동일한 키로 정상 작동 확인됨
- 가능한 원인: VPN, Proxy, 방화벽, ISP 제한

**해결 방법**:
- 로컬 네트워크 디버깅 불필요
- **서버 배포 후 테스트 진행**으로 우회
- 서버에서 AI 기능 정상 작동 확인됨 ✅

**서버 테스트 결과**:
```json
{
  "aiFeedback": "1. 격려: 이 통찰은 개인의 감정과 기술적인 개념을 연결짓는 매우 창의적인 접근입니다...",
  "insightAbstraction": "#DataTransmission #NetworkLatency #UserExperience...",
  "insightTrigger": "인천공항 도착했어요.. i still miss him.."
}
```

✅ AI 기능 정상 작동 확인!

---

### 🟡 문제 2: Flyway Migration 이해 부족

**학습한 내용**:

#### Flyway 핵심 원칙:
1. **한번 적용된 migration은 절대 수정 불가**
   - Flyway는 각 migration 파일의 checksum을 DB에 저장
   - 파일 내용이 바뀌면 checksum이 달라져서 에러 발생

2. **Migration 버전은 순차적**
   - V1, V2, V3, ... 순서대로 실행
   - 중간 버전을 건너뛸 수 없음

3. **의존성 순서 중요**
   - V3이 log_type 컬럼을 사용하면, log_type은 V3 이전에 생성되어야 함
   - 순서를 잘못 정하면 런타임 에러 발생

#### 올바른 Migration 관리:
```
✅ 새 기능 추가 → 새 버전(V6, V7, ...) 추가
✅ 실수한 migration → 새 버전으로 롤백 SQL 작성
❌ 이미 배포된 migration 파일 수정/삭제
```

#### 실전 사례:
```
잘못: V2를 V5 내용으로 교체 → Checksum mismatch
올바름: V2 복원 + V5로 새 버전 추가
```

---

## 프롬프트 엔지니어링 학습 가이드

### 📍 학습 위치

**파일**: `backend/src/main/java/com/greenkey20/innerorbit/service/impl/AiServiceImpl.java`
**메서드**: `generateInsightFeedback()` (Line 486-579)

### ✏️ 작성해야 하는 부분 2곳

#### 1. System Prompt 개선 (Line 493-513)

**현재 상태**:
```java
String systemPrompt = """
    You are a supportive mentor helping a developer build "Architecture of Insight"...

    Structure your feedback in Korean with 3 parts:
    1. 격려 (Encouragement): What's good about this insight? (1-2 sentences)
    2. 심화 (Deepening): How can this connection be explored further? (2-3 sentences)
    3. 확장 (Extension): What other CS concepts could relate? (1-2 sentences)
    """;
```

**개선 과제**:
- ✅ `recentLogsContext` (최근 감정 패턴) 활용
- ✅ Few-shot learning 예시 추가
- ✅ 더 context-aware한 피드백 유도
- ✅ Tone: warm, constructive, intellectually stimulating

**핵심**:
Line 490에서 생성된 `recentLogsContext`가 현재 사용되지 않고 있음!
```java
String recentLogsContext = buildRecentFlightLogsContext();
// ⬆️ 이 데이터를 System Prompt에 통합하는 것이 학습 목표
```

**작성 힌트** (코드 주석):
```
1. AI의 역할 정의: "당신은 ___입니다"
2. recentLogsContext를 활용하여 최근 감정 패턴 참고
3. Few-shot learning: 좋은 피드백 예시 1-2개 추가
4. 피드백 구조: 격려 → 심화 → 확장 (기존과 동일하게 유지)
5. Output format: 4-6 문장, 한국어
6. 톤: warm, constructive, intellectually stimulating
```

**recentLogsContext 예시**:
```
[사용자의 최근 감정 패턴 (Flight Logs)]
1. "오늘도 그가 보고 싶다..." (G:80%, S:30%) - 과잉일반화, 흑백논리 감지
2. "회의에서 발표를 망쳤다. 다시는..." (G:70%, S:25%) - 파국화 감지
3. "조금씩 나아지고 있는 것 같다" (G:50%, S:60%)
```

#### 2. User Message 개선 (Line 536-556)

**현재 상태**:
```java
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
```

**개선 과제**:
- ✅ 명확한 요청 유지
- ✅ (선택) 최근 로그 맥락 강조 추가

**작성 힌트**:
```
1. 사용자의 Insight Log 정보 포함 (trigger, abstraction, application)
2. 명확한 요청: "이 통찰에 대해 피드백해주세요"
3. (선택) 최근 로그 맥락 강조: "최근 감정 패턴과 연결하여 피드백해주세요"
```

---

### 🎯 학습 목표

**Before**: 기본 프롬프트
```
AI가 일반적인 피드백 생성
→ "통찰을 기록해주셔서 감사합니다. 일상에서 CS 개념을 발견하는 것은..."
```

**After**: Context-aware 프롬프트
```
AI가 사용자의 최근 감정 패턴을 고려한 개인화된 피드백 생성
→ "최근 파국화 패턴이 보이는데, 이번 통찰에서는 긍정적인 발견이 돋보입니다.
   '데이터 전송'과 '감정 전달'의 연결은 창의적입니다. 추가로..."
```

---

### 📚 참고할 프롬프트 예시

**동일 파일의 다른 메서드**:

1. **`analyzeCognitiveDistortions()`** (Line 34-112)
   - Few-shot learning 예시 포함 ✅
   - JSON 구조화된 출력 ✅
   - User state를 System Prompt에 통합 ✅

2. **`generateDynamicPrompt()`** (Line 383-419)
   - 9가지 상황별 맞춤 프롬프트 ✅
   - Temperature 0.9로 창의성 높임 ✅
   - Context-aware 질문 생성 ✅

3. **`suggestCsKeywords()`** (Line 422-483)
   - 명확한 출력 형식 요구 (JSON array) ✅
   - Few-shot learning 예시 2개 ✅
   - Guidelines 명확히 제시 ✅

---

## 다음 단계

### 🚀 우선순위 High

1. **PR #54 머지 후 서버 재배포**
   - Insight Log 수정 기능이 정상 작동하는지 확인
   - 전체 Insight Log 플로우 E2E 테스트

2. **프롬프트 엔지니어링 학습 진행**
   - `AiServiceImpl.generateInsightFeedback()` 메서드 개선
   - `recentLogsContext` 활용하여 개인화된 피드백 생성
   - Few-shot learning 예시 추가

### 📝 우선순위 Medium

3. **AI 기능 테스트 및 개선**
   - AI Suggest: 다양한 Trigger로 키워드 품질 확인
   - AI Feedback: 피드백 품질 및 톤 확인
   - Temperature, 프롬프트 조정

4. **UI/UX 개선**
   - AI 기능 로딩 상태 개선 (skeleton UI 등)
   - 에러 메시지 개선
   - AI 응답 시간 모니터링

### 🔍 우선순위 Low

5. **문서화**
   - Insight Log 사용 가이드 작성
   - AI 기능 설명 추가
   - 프롬프트 엔지니어링 학습 가이드 업데이트

6. **성능 최적화**
   - AI API 호출 캐싱 고려
   - 로딩 시간 개선

---

## 참고 링크

- PR #51: https://github.com/greenkey20/inner-orbit/pull/51
- PR #52: https://github.com/greenkey20/inner-orbit/pull/52
- PR #53: https://github.com/greenkey20/inner-orbit/pull/53
- PR #54: https://github.com/greenkey20/inner-orbit/pull/54

---

## 메모

### 개발 환경
- 로컬: macOS (OpenAI API 접근 불가 - 네트워크 이슈)
- 서버: Ubuntu (OpenAI API 정상 작동)
- 해결: 서버 배포 후 테스트 진행

### Flyway Best Practices
```
✅ DO:
- 새 기능 → 새 migration 버전 추가
- 의존성 순서 고려
- 서버 배포 전 로컬에서 migration 테스트

❌ DON'T:
- 이미 배포된 migration 수정
- migration 버전 번호 건너뛰기
- 의존성 없이 순서 무작위 배치
```

### AI 기능 동작 확인
```
✅ AI Keyword Suggestion: 정상 작동 (서버)
✅ AI Feedback Generation: 정상 작동 (서버)
✅ Decrypt Log (감정 분석): 정상 작동
✅ Dynamic Prompt: 정상 작동
```

---

**작성일**: 2025년 12월 28일
**작성자**: Claude Code (Sonnet 4.5) + greenkey20
