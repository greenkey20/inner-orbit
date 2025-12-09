# Inner Orbit System

**Gravity Assist Protocol: AI 기반 감정 로그 애플리케이션**

Inner Orbit는 우주 비행의 메타포를 활용한 감정 일지 및 자기 성찰 도구입니다. 사용자의 감정 상태를 "궤도 안정성(Stability)"과 "중력(Gravity)" 지표로 시각화하고, AI를 통해 심리적 패턴과 인지 왜곡을 분석합니다.

## 주요 기능

### 감정 로깅
- **안정성(Stability) & 중력(Gravity)** 지표를 통한 감정 상태 기록
- 자유로운 텍스트 입력으로 일상과 감정 기록
- AI 기반 프롬프트 어시스턴트로 작성 가이드 제공

### AI 분석
- **백엔드 기반 OpenAI API 통합** (보안 강화)
- 인지 왜곡(Cognitive Distortions) 감지 및 피드백
- Gravity/Stability 상태 기반 동적 프롬프트 생성
- 개인화된 통찰과 조언 제공

### 시각화 & 분석
- **Flight Trajectory**: 시간에 따른 감정 궤적 그래프
- **Analytics Dashboard**: 안정성, 중력 분포 및 트렌드 분석
- 인지 왜곡 패턴 분석 및 통계

### 데이터 관리
- 로그 히스토리 조회, 수정, 삭제
- JSON 형식 데이터 내보내기/가져오기
- PostgreSQL 기반 영구 저장

## 기술 스택

### Backend
- **Java 21** with Spring Boot 3.5.8
- **Spring Data JPA** - 데이터베이스 레이어
- **Spring AI** - OpenAI 통합
- **PostgreSQL** - 관계형 데이터베이스
- **Lombok** - 보일러플레이트 코드 감소
- **Gradle** - 빌드 도구

### Frontend
- **React 19** - UI 라이브러리
- **Vite** - 빌드 도구 및 개발 서버
- **Tailwind CSS** - 유틸리티 우선 CSS 프레임워크
- **Recharts** - 데이터 시각화
- **Lucide React** - 아이콘 라이브러리

### Infrastructure
- **Docker & Docker Compose** - 컨테이너화된 개발 환경
- **PostgreSQL Container** - 격리된 데이터베이스 환경

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
│   │   │   │   │   └── AiService.java (동적 프롬프트 생성)
│   │   │   │   ├── repository/      # 데이터 액세스 레이어
│   │   │   │   ├── domain/
│   │   │   │   │   ├── entity/      # JPA 엔티티
│   │   │   │   │   └── dto/         # 데이터 전송 객체
│   │   │   │   └── exception/       # 예외 처리
│   │   │   └── resources/
│   │   └── test/
│   ├── build.gradle
│   └── settings.gradle
│
├── frontend/                   # React 프론트엔드
│   ├── src/
│   │   ├── components/         # UI 컴포넌트
│   │   │   ├── Header.jsx
│   │   │   ├── StatusDashboard.jsx
│   │   │   ├── LogEditor.jsx
│   │   │   ├── LogHistory.jsx
│   │   │   ├── PromptAssistant.jsx
│   │   │   ├── FlightTrajectory.jsx
│   │   │   ├── Analytics.jsx
│   │   │   └── ...
│   │   ├── hooks/              # Custom React Hooks
│   │   ├── services/           # API 서비스
│   │   │   └── openaiService.js (백엔드 API 호출)
│   │   ├── utils/              # 유틸리티 함수
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

### Log Management

로그 엔트리의 CRUD(생성, 조회, 수정, 삭제) 기능을 제공합니다.

- `GET /api/logs` - 모든 로그 엔트리 조회
- `POST /api/logs` - 새 로그 엔트리 생성
- `PUT /api/logs/{id}` - 로그 엔트리 수정
- `DELETE /api/logs/{id}` - 로그 엔트리 삭제

### AI Services

OpenAI API를 활용한 AI 기반 분석 및 프롬프트 생성 기능을 제공합니다.

- `POST /api/ai/analyze/{logId}` - **AI 인지 왜곡 분석 실행**
  - **설명**: 로그 내용을 AI로 분석하여 인지 왜곡(Cognitive Distortions) 감지 및 재구성 제안
  - **파라미터**: `logId` - 분석할 로그 엔트리 ID
  - **응답**: `{"distortions": [...], "reframed": "...", "alternative": "..."}`

- `PATCH /api/ai/analysis/{logId}` - **분석 결과 수동 업데이트**
  - **설명**: 로그 엔트리의 AI 분석 결과를 수동으로 추가/수정
  - **파라미터**: `logId` - 로그 엔트리 ID
  - **요청 본문**: `{"distortions": [...], "reframed": "...", "alternative": "..."}`
  - **응답**: 업데이트된 로그 엔트리 전체 정보

- `GET /api/ai/prompt?gravity={0-100}&stability={0-100}` - **동적 프롬프트 생성**
  - **설명**: 사용자의 현재 Gravity/Stability 상태에 맞는 맞춤형 질문 생성
  - **파라미터**:
    - `gravity`: 외부 인력 (0-100, 기본값: 50)
    - `stability`: 코어 안정성 (0-100, 기본값: 50)
  - **응답**: `{"prompt": "생성된 한국어 질문"}`

## 개발

### 백엔드 빌드

```bash
cd backend
./gradlew build
```

### 백엔드 테스트

```bash
./gradlew test
```

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
- 모든 AI 기능(분석, 프롬프트 생성)은 백엔드 REST API를 통해 제공
- `localStorage`에 민감 정보 저장하지 않음

### 주요 변경사항 (v2.0)

- **보안 개선**: OpenAI API 키 관리를 프론트엔드에서 백엔드로 이전 (#23)
- **새 API**: `/api/ai/prompt` 엔드포인트 추가 - 동적 프롬프트 생성
- **의존성 감소**: 프론트엔드에서 `openai` npm 패키지 제거
- **Docker 지원**: Docker Compose를 통한 간편한 개발 환경 구성
- **환경 변수 표준화**: `SPRING_DATASOURCE_*` 형식으로 통일

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재 개인 프로젝트로 운영 중입니다.

---

**Inner Orbit** - 감정의 궤도를 탐험하고, AI와 함께 내면의 우주를 항해하세요.
