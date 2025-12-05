# Inner Orbit System

**Gravity Assist Protocol: AI 기반 감정 로그 애플리케이션**

Inner Orbit는 우주 비행의 메타포를 활용한 감정 일지 및 자기 성찰 도구입니다. 사용자의 감정 상태를 "궤도 안정성(Stability)"과 "중력(Gravity)" 지표로 시각화하고, AI를 통해 심리적 패턴과 인지 왜곡을 분석합니다.

## 주요 기능

### 감정 로깅
- **안정성(Stability) & 중력(Gravity)** 지표를 통한 감정 상태 기록
- 자유로운 텍스트 입력으로 일상과 감정 기록
- AI 기반 프롬프트 어시스턴트로 작성 가이드 제공

### AI 분석
- OpenAI API를 활용한 감정 분석
- 인지 왜곡(Cognitive Distortions) 감지 및 피드백
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
- **OpenAI API** - 클라이언트 측 AI 통합

## 프로젝트 구조

```
inner-orbit-system/
├── backend/                    # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/greenkey20/innerorbit/
│   │   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   │   ├── service/         # 비즈니스 로직
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
└── frontend/                   # React 프론트엔드
    ├── src/
    │   ├── components/         # UI 컴포넌트
    │   │   ├── Header.jsx
    │   │   ├── StatusDashboard.jsx
    │   │   ├── LogEditor.jsx
    │   │   ├── LogHistory.jsx
    │   │   ├── FlightTrajectory.jsx
    │   │   ├── Analytics.jsx
    │   │   └── ...
    │   ├── hooks/              # Custom React Hooks
    │   ├── services/           # API 서비스
    │   ├── utils/              # 유틸리티 함수
    │   ├── App.jsx             # 메인 앱 컴포넌트
    │   └── main.jsx            # 앱 엔트리 포인트
    ├── package.json
    └── vite.config.js
```

## 시작하기

### 사전 요구사항

- **Java 21** 이상
- **Node.js 18** 이상
- **PostgreSQL 14** 이상
- **OpenAI API Key**

### 설치 및 실행

#### 1. 저장소 클론

```bash
git clone <repository-url>
cd inner-orbit-system
```

#### 2. 데이터베이스 설정

PostgreSQL에 데이터베이스를 생성합니다:

```sql
CREATE DATABASE innerorbit;
```

#### 3. 백엔드 설정

`backend/src/main/resources/application.properties` 또는 `application.yml` 파일을 생성하고 다음 설정을 추가합니다:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/innerorbit
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# OpenAI
spring.ai.openai.api-key=your_openai_api_key
spring.ai.openai.chat.model=gpt-4

# Server
server.port=8080
```

백엔드 서버 실행:

```bash
cd backend
./gradlew bootRun
```

#### 4. 프론트엔드 설정

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

- `GET /api/logs` - 모든 로그 엔트리 조회
- `POST /api/logs` - 새 로그 엔트리 생성
- `PUT /api/logs/{id}` - 로그 엔트리 수정
- `DELETE /api/logs/{id}` - 로그 엔트리 삭제
- `PUT /api/logs/{id}/analysis` - 로그 분석 업데이트

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

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

## 기여

현재 개인 프로젝트로 운영 중입니다.

---

**Inner Orbit** - 감정의 궤도를 탐험하고, AI와 함께 내면의 우주를 항해하세요.
