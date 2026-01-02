# Inner Orbit System

**Gravity Assist Protocol: AI 기반 감정 로그 애플리케이션**

Inner Orbit는 우주 비행의 메타포를 활용한 감정 일지 및 자기 성찰 도구입니다. 사용자의 감정 상태를 "궤도 안정성(Stability)"과 "중력(Gravity)" 지표로 시각화하고, AI를 통해 심리적 패턴과 인지 왜곡을 분석합니다.

## 주요 기능

### 📝 세 가지 로깅 모드

Inner Orbit는 세 가지 로깅 모드를 제공하여 다양한 기록 요구사항을 충족합니다:

#### 1. Daily Mode (일반 모드)
- **안정성(Stability) & 중력(Gravity)** 지표를 통한 감정 상태 기록
- 자유로운 텍스트 입력으로 일상과 감정 기록
- AI 기반 Navigation Query로 작성 가이드 제공
- **AI 인지 왜곡 분석**: 일기 내용에서 인지 왜곡 패턴을 감지하고 CBT 기반 피드백 제공
- 녹색(green) 테마

#### 2. Sensory Mode (감각 모드 / Travel Mode)
- 여행이나 특별한 경험을 감각적 디테일과 함께 기록
- **위치 정보**: 장소 또는 여행지 (최대 500자)
- **3가지 감각 정보** 각 최대 10,000자:
  - 👁️ 시각: 풍경, 색상, 빛의 느낌
  - 👂 청각: 소리, 분위기
  - ✋ 촉각: 온도, 바람, 질감
- 라임 그린(lime/emerald) 테마
- 여행 일지, 명상 기록, 자연 체험 등에 활용

#### 3. Insight Mode (통찰 모드 / Architecture of Insight)
- 일상 관찰을 컴퓨터 과학 개념과 연결하여 통찰 쌓기
- **3단계 구조**:
  1. **Observation (관찰)**: 일상에서 관찰한 것 기록
  2. **Abstraction (CS 개념 연결)**: 관찰과 연결되는 CS 개념 (예: Race Condition, Load Balancer)
  3. **Application (실무 적용)**: 개념을 실제 코드나 프로젝트에 적용하는 방법
- **AI 키워드 추천**: 관찰 내용 기반 CS 개념 자동 제안
- **AI 피드백**: 3단계 내용을 종합하여 심화 학습 제안 및 피드백 제공
- 청록색(teal/cyan) 테마

### 🤖 AI 기능

#### Navigation Query (항해 질문)
- **Static Mode**: 사전 정의된 6가지 질문 제공
- **AI Request Mode**: Gravity/Stability 상태 기반 동적 질문 생성
  - 현재 감정 상태에 맞춤화된 질문 자동 생성
  - 탭 클릭 시 자동 요청으로 UX 개선

#### Cognitive Distortion Analysis (인지 왜곡 분석)
- Daily Mode 로그에 대한 AI 분석 제공
- 인지행동치료(CBT) 기반 왜곡 패턴 감지
- 재구성(Reframed) 및 대안적 관점(Alternative) 제시
- OpenAI GPT-4o-mini 모델 사용

#### Insight Feedback (통찰 피드백)
- Insight Mode 로그에 대한 AI 피드백
- 관찰-개념-적용의 연결성 평가
- CS 개념 심화 학습 방향 제안
- 실무 적용 가능성 검토

### 📊 시각화 & 분석

- **Flight Trajectory**: 시간에 따른 감정 궤적 그래프 (Recharts)
- **Analytics Dashboard**: 안정성, 중력 분포 및 트렌드 분석
- 인지 왜곡 패턴 분석 및 통계
- ISO 8601 표준 날짜 형식으로 데이터 일관성 보장

### 💾 데이터 관리

- 로그 히스토리 조회, 수정, 삭제
- JSON 형식 데이터 내보내기/가져오기
- PostgreSQL 기반 영구 저장
- 무한 스크롤 페이지네이션
- **ISO 8601 날짜 형식**: 백엔드 API는 국제 표준 형식 사용, 프론트엔드는 한국어 형식으로 표시
- **실시간 업데이트**: JPA flush()를 통한 즉각적인 updatedAt 반영

## 기술 스택

### Backend
- **Java 21** with Spring Boot 3.5.8
- **Spring Data JPA** - 데이터베이스 레이어
- **Spring AI** - OpenAI 통합 (GPT-4o-mini)
- **PostgreSQL** - 관계형 데이터베이스
- **Lombok** - 보일러플레이트 코드 감소
- **Hypersistence Utils** - JSON 타입 매핑 및 PostgreSQL 지원
- **Gradle** - 빌드 도구
- **H2 Database** - 통합 테스트용 인메모리 DB
- **Flyway** - 데이터베이스 마이그레이션

### Frontend
- **React 19** - UI 라이브러리
- **Vite** - 빌드 도구 및 개발 서버
- **Tailwind CSS** - 유틸리티 우선 CSS 프레임워크
- **Recharts** - 데이터 시각화
- **Lucide React** - SVG 아이콘 라이브러리

### Infrastructure
- **Docker & Docker Compose** - 컨테이너화된 개발 환경
- **PostgreSQL 16** - 격리된 데이터베이스 환경
- **Nginx** - 프론트엔드 서빙

## 프로젝트 구조

```
inner-orbit-system/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/greenkey20/innerorbit/
│   │   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   │   │   ├── LogController.java
│   │   │   │   │   └── AiController.java
│   │   │   │   ├── service/         # 비즈니스 로직
│   │   │   │   │   ├── LogService.java
│   │   │   │   │   ├── LogServiceImpl.java
│   │   │   │   │   ├── AiService.java
│   │   │   │   │   └── AiServiceImpl.java
│   │   │   │   ├── repository/      # 데이터 액세스 레이어
│   │   │   │   │   └── LogRepository.java
│   │   │   │   ├── domain/
│   │   │   │   │   ├── entity/      # JPA 엔티티
│   │   │   │   │   │   ├── LogEntry.java
│   │   │   │   │   │   └── LogType.java (DAILY, SENSORY, INSIGHT)
│   │   │   │   │   └── dto/         # 데이터 전송 객체
│   │   │   │   │       ├── request/
│   │   │   │   │       │   ├── LogEntryCreateRequest.java
│   │   │   │   │       │   ├── LogEntryUpdateRequest.java
│   │   │   │   │       │   └── AnalysisUpdateRequest.java
│   │   │   │   │       └── response/
│   │   │   │   │           ├── LogEntryResponse.java
│   │   │   │   │           ├── AnalysisResult.java
│   │   │   │   │           └── DistortionDto.java
│   │   │   │   └── exception/       # 예외 처리
│   │   │   │       ├── ErrorCode.java
│   │   │   │       ├── BusinessException.java
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   └── resources/
│   │   │       ├── application.yaml
│   │   │       └── db/migration/    # Flyway 마이그레이션
│   │   └── test/                # 통합 테스트
│   │       └── java/
│   │           ├── DeepLogIntegrationTest.java
│   │           └── InsightLogIntegrationTest.java
│   ├── build.gradle
│   └── settings.gradle
│
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── components/         # UI 컴포넌트
│   │   │   ├── Header.jsx
│   │   │   ├── StatusDashboard.jsx
│   │   │   ├── LogEditor.jsx          # 3가지 모드 로그 입력
│   │   │   ├── LogHistory.jsx         # 로그 표시 및 수정
│   │   │   ├── PromptAssistant.jsx    # Navigation Query
│   │   │   ├── FlightTrajectory.jsx   # 감정 궤적 차트
│   │   │   ├── Analytics.jsx
│   │   │   └── TelemetryGuide.jsx
│   │   ├── hooks/              # Custom React Hooks
│   │   │   └── useInnerOrbit.js    # 로그 상태 관리
│   │   ├── services/           # API 서비스
│   │   │   └── openaiService.js    # 백엔드 API 호출
│   │   ├── utils/              # 유틸리티 함수
│   │   │   └── analytics.js
│   │   ├── App.jsx             # 메인 앱 컴포넌트
│   │   └── main.jsx            # 앱 엔트리 포인트
│   ├── package.json
│   └── vite.config.js
│
├── docker-compose.yml          # Docker Compose 설정
└── .env                        # 환경 변수 (Git에서 제외)
```

## 시작하기

### 사전 요구사항

- **Java 21** 이상
- **Node.js 18** 이상
- **PostgreSQL 14** 이상
- **OpenAI API Key**

### 설치 및 실행

#### 방법 1: Docker Compose 사용 (권장)

가장 빠르고 간편한 방법입니다.

**1. 저장소 클론**

```bash
git clone <repository-url>
cd inner-orbit-system
```

**2. 환경 변수 설정**

프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가합니다:

```env
# Database Configuration
DB_NAME=innerorbit_db
DB_USER=postgres
DB_PASSWORD=your_secure_password

# OpenAI API Key (필수)
OPENAI_API_KEY=sk-proj-your-api-key-here
```

**3. Docker Compose 실행**

```bash
docker-compose up -d
```

이제 다음 주소에서 애플리케이션에 접근할 수 있습니다:
- 프론트엔드: `http://localhost:5173`
- 백엔드 API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

**4. 중지 및 재시작**

```bash
# 중지
docker-compose down

# 재시작
docker-compose up -d
```

---

#### 방법 2: 로컬 개발 환경

**1. 저장소 클론**

```bash
git clone <repository-url>
cd inner-orbit-system
```

**2. 데이터베이스 설정**

PostgreSQL에 데이터베이스를 생성합니다:

```sql
CREATE DATABASE innerorbit_db;
```

**3. 백엔드 설정**

환경 변수를 설정하거나 `backend/src/main/resources/application.yaml`을 수정합니다:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/innerorbit_db}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:postgres}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your_openai_api_key}
      chat:
        options:
          model: gpt-4o-mini
```

백엔드 서버 실행:

```bash
cd backend
./gradlew bootRun
```

**4. 프론트엔드 설정**

의존성 설치:

```bash
cd frontend
npm install
```

프론트엔드 개발 서버 실행:

```bash
npm run dev
```

애플리케이션이 `http://localhost:5173`에서 실행됩니다.

## API 엔드포인트

### Log Entries

- `GET /api/logs` - 모든 로그 엔트리 조회 (최신순)
- `POST /api/logs` - 새 로그 엔트리 생성
- `GET /api/logs/{id}` - 특정 로그 조회
- `PUT /api/logs/{id}` - 로그 엔트리 수정
- `DELETE /api/logs/{id}` - 로그 엔트리 삭제
- `POST /api/logs/{id}/analyze` - AI 기반 인지 왜곡 분석 (Daily Mode)
- `POST /api/logs/{id}/insights/feedback` - AI 피드백 생성 (Insight Mode)
- `GET /api/logs/statistics` - 통계 조회

### AI Services

- `GET /api/ai/prompt?gravity={0-100}&stability={0-100}` - 동적 Navigation Query 생성
  - **설명**: 사용자의 현재 Gravity/Stability 상태에 맞는 맞춤형 질문 생성
  - **파라미터**:
    - `gravity`: 외부 인력 (0-100, 기본값: 50)
    - `stability`: 코어 안정성 (0-100, 기본값: 50)
  - **응답**: `{"prompt": "생성된 한국어 질문"}`

- `POST /api/ai/insights/suggest-keywords` - Insight Mode AI 키워드 추천
  - **설명**: 관찰 내용을 기반으로 관련 CS 개념 자동 제안
  - **요청 본문**: `{"trigger": "관찰 내용"}`
  - **응답**: `{"keywords": ["RaceCondition", "ConcurrencyControl", ...]}`

## 데이터베이스 스키마

### LogEntry 테이블

**기본 필드**:
- `id` (BIGINT, Primary Key)
- `content` (TEXT) - 로그 내용
- `stability` (INTEGER, 0-100) - 안정성 지표
- `gravity` (INTEGER, 0-100) - 중력 지표
- `created_at` (TIMESTAMP) - ISO 8601 형식
- `updated_at` (TIMESTAMP) - ISO 8601 형식
- `user_id` (BIGINT)
- `analysis_result` (JSONB) - AI 분석 결과

**Sensory Mode 필드**:
- `location` (VARCHAR 500) - 여행지/장소
- `sensory_visual` (TEXT) - 시각 정보
- `sensory_auditory` (TEXT) - 청각 정보
- `sensory_tactile` (TEXT) - 촉각 정보

**Insight Mode 필드**:
- `insight_abstraction` (TEXT) - CS 개념 연결
- `insight_application` (TEXT) - 실무 적용
- `ai_feedback` (TEXT) - AI 피드백 결과

**Log Type**:
- `log_type` (VARCHAR 20) - DAILY, SENSORY, INSIGHT
- `is_deep_log` (BOOLEAN, deprecated) - 하위 호환성 유지용

모든 모드별 필드는 nullable로 설계되어 세 가지 모드 간의 호환성을 보장합니다.

## 개발

### 백엔드 빌드

```bash
cd backend
./gradlew build
```

### 백엔드 테스트

**통합 테스트 실행**:

```bash
./gradlew test
```

**테스트 커버리지**:
- `DeepLogIntegrationTest.java` - Sensory Mode 기능 통합 테스트
- `InsightLogIntegrationTest.java` - Insight Mode 기능 통합 테스트
- H2 인메모리 데이터베이스를 사용한 격리된 테스트 환경

### 프론트엔드 빌드

```bash
cd frontend
npm run build
```

### 프론트엔드 린트

```bash
npm run lint
```

## 보안 및 아키텍처

### OpenAI API 키 관리

이 프로젝트는 **보안 강화**를 위해 OpenAI API 키를 백엔드에서 관리합니다:

- API 키는 백엔드 환경 변수(`OPENAI_API_KEY`)로 관리
- 프론트엔드에서 API 키가 노출되지 않음
- 모든 AI 기능(분석, 프롬프트 생성, 키워드 추천, 피드백)은 백엔드 REST API를 통해 제공
- `localStorage`에 민감 정보 저장하지 않음

### 아키텍처 특징

- **계층화 구조**: Controller → Service → Repository → Entity
- **DTO 패턴**: 요청/응답 데이터 분리로 유연성 향상
- **글로벌 예외 처리**: 통일된 에러 응답 형식
- **통합 테스트**: H2 인메모리 DB로 데이터베이스 레벨 검증
- **Flyway 마이그레이션**: 데이터베이스 스키마 버전 관리
- **ISO 8601 표준**: 국제 표준 날짜 형식으로 시간대 문제 해결
- **JPA Flush 최적화**: 즉각적인 엔티티 상태 반영

## 버전 히스토리

### v4.0 (2026-01-03)
- **ISO 8601 날짜 형식 표준화** (#59)
  - 백엔드 API 응답을 ISO 8601 형식으로 변경
  - 프론트엔드에서 한국어 형식으로 변환하여 표시
  - 날짜 파싱 로직 단순화
  - JPA flush()를 통한 실시간 updatedAt 반영

### v3.5 (2026-01-02)
- **UI/UX 개선** (#58)
  - Insight Log 색상 스키마 변경 (violet → teal/cyan)
  - Navigation Query 자동 생성 UX 개선
  - Static 탭 아이콘 변경 (RefreshCw → List)

### v3.4 (2026-01-01)
- **Insight Log 개선** (#56, #57)
  - insightTrigger → content 필드로 통합
  - Edit 모드 UI 개선
  - AI Feedback "View Analysis" 버튼 표시 문제 해결

### v3.0 (2025-12-30)
- **Insight Log (Architecture of Insight) 추가** (#47, #48, #49, #50, #51)
  - 일상 관찰 → CS 개념 연결 → 실무 적용 3단계 구조
  - AI 키워드 추천 기능 (GPT-4o-mini)
  - AI 피드백 생성 기능
  - 청록색(teal/cyan) 테마로 시각적 구분
  - 백엔드 엔티티 및 DTO 확장
  - 통합 테스트 작성

### v2.5 (2025-12-15)
- **Sensory Mode (Travel Mode) 추가** (#26, #27, #28)
  - 위치 정보 및 3가지 감각(시각, 청각, 촉각) 입력 지원
  - 백엔드 엔티티 및 DTO 확장
  - 프론트엔드 UI 구현 (라임 그린 테마)
  - 통합 테스트 작성
- 일반 모드와의 호환성 보장

### v2.0
- **OpenAI API 키 관리 백엔드 이전** (#23)
- 동적 Navigation Query 생성 API 추가
- Docker Compose 지원
- 환경 변수 표준화

### v1.0
- 초기 릴리스
- Daily Mode 로그 기능
- Gravity/Stability 지표 시각화
- AI 인지 왜곡 분석

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재 개인 프로젝트로 운영 중입니다.

---

**Inner Orbit** - 감정의 궤도를 탐험하고, AI와 함께 내면의 우주를 항해하세요.
