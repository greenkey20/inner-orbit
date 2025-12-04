# 🌌 Inner Orbit

> **Gravity Assist Protocol**: Converting emotional gravity into growth momentum.  
> 감정의 인력을 성장의 동력으로 전환합니다.

[![React](https://img.shields.io/badge/React-18.3-61DAFB?logo=react)](https://react.dev)
[![Vite](https://img.shields.io/badge/Vite-6.0-646CFF?logo=vite)](https://vite.dev)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-3.4-06B6D4?logo=tailwindcss)](https://tailwindcss.com)

감정 로그를 통한 **인지 재구성(Cognitive Restructuring)**을 돕는 감정 일지 애플리케이션입니다.

---

## ✨ Features

### 📝 Core Features
- **Emotion Logging**: Gravity(외부 인력)와 Stability(코어 안정성) 메트릭으로 감정 상태 기록
- **Log History**: 타임라인 형식의 로그 히스토리 (Infinite Scroll 지원)
- **Log Editing**: 작성된 로그 수정 및 audit trail (수정 일시 추적)
- **Data Persistence**: localStorage 기반 자동 저장 (백업/복원 기능 포함)
- **Telemetry Guide**: Flight Telemetry 개념(Gravity/Stability)에 대한 인앱 가이드 제공

### 🎨 UI/UX
- **Custom Theme**: Emerald (Stability) + Indigo (Gravity) 색상 시스템
- **Responsive Design**: 모바일 최적화 (max-width: 448px)
- **PWA Support**: Progressive Web App 지원
- **Smooth Animations**: Tailwind transitions

### 🤖 AI Features
- **Prompt Assistant**: 감정 정리를 돕는 AI 쿼리 제안
- **Contextual Suggestions**: 상황별 맞춤 질문 제공

---

## 🎨 Color Theme

우주의 중력 렌즈를 모티브로 한 커스텀 색상 시스템:

```javascript
// tailwind.config.js
colors: {
  primary: colors.emerald,    // Stability (안정성)
  secondary: colors.indigo,   // Gravity (외부 인력)
}
```

- **Primary (Emerald)**: 성장, 안정성, 균형을 상징
- **Secondary (Indigo)**: 깊이, 통찰, 내면의 힘을 상징

---

## 🏗️ Architecture

### Modular Architecture with Separation of Concerns

```
src/
├── components/           # UI Components
│   ├── Header.jsx       # 앱 헤더
│   ├── StatusDashboard.jsx   # Gravity/Stability 슬라이더
│   ├── LogEditor.jsx    # 로그 작성 폼
│   ├── LogHistory.jsx   # 로그 목록 및 편집
│   └── PromptAssistant.jsx   # AI 질문 제안
├── hooks/
│   └── useInnerOrbit.js # 비즈니스 로직 & 상태 관리
├── App.jsx              # Composition Root
└── main.jsx             # Entry Point
```

### Design Principles
- **Separation of Concerns**: 로직(hooks) ↔ UI(components) 분리
- **Immutability**: 불변성 기반 상태 업데이트
- **Single Responsibility**: 각 컴포넌트는 하나의 책임만 수행

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ 
- npm or yarn

### Installation

```bash
# Clone repository
git clone https://github.com/greenkey20/inner-orbit.git
cd inner-orbit

# Install dependencies
npm install

# Run development server
npm run dev
```

서버가 시작되면 브라우저에서 `http://localhost:5173` 접속

### Build for Production

```bash
npm run build
npm run preview
```

---

## 📊 Data Structure

### Log Entry Schema

```javascript
{
  id: 1733288400000,           // timestamp (unique)
  date: "2024년 12월 4일 09:00", // 작성 일시
  content: "오늘의 감정 로그",   // 텍스트 내용
  gravity: 50,                  // 외부 인력 (0-100)
  stability: 70,                // 코어 안정성 (0-100)
  updatedAt: "2024년 12월 4일 10:30"  // 수정 일시 (optional)
}
```

### Storage
- **Engine**: localStorage
- **Key**: `journalEntries`
- **Format**: JSON array

---

## 🎯 Metrics Explained

### Gravity (외부 인력) ⚡
외부 환경이나 타인으로부터 받는 감정적 영향력
- **High**: 외부 요인에 크게 영향받는 상태
- **Low**: 외부 요인에 덜 영향받는 상태

### Stability (코어 안정성) 🛡️
내면의 평온함과 정서적 안정성
- **High**: 감정적으로 안정되고 균형잡힌 상태
- **Low**: 내적 동요나 불안정한 상태

---

## 🛠️ Tech Stack

### Core
- **React 18.3** - UI Library
- **Vite 6.0** - Build Tool & Dev Server
- **Tailwind CSS 3.4** - Utility-first CSS

### Icons & Assets
- **Lucide React** - Icon Library
- **Custom Assets** - `public/gravity.jpg` header image

### Development
- **ESLint** - Code Linting
- **PostCSS** - CSS Processing

---

## 📦 Project Structure

```
inner-orbit/
├── public/
│   ├── gravity.jpg          # Header background
│   ├── manifest.json        # PWA manifest
│   └── ios-icon.png        # iOS icon
├── src/
│   ├── components/         # React components
│   ├── hooks/             # Custom hooks
│   ├── App.jsx            # Main app component
│   ├── index.css          # Global styles
│   └── main.jsx           # Entry point
├── tailwind.config.js     # Tailwind configuration
├── vite.config.js         # Vite configuration
└── package.json
```

---

## 🔧 Configuration

### Tailwind Custom Colors

```javascript
// tailwind.config.js
import colors from 'tailwindcss/colors';

export default {
  theme: {
    extend: {
      colors: {
        primary: colors.emerald,
        secondary: colors.indigo,
      },
    },
  },
}
```

### PWA Manifest

앱은 PWA로 설치 가능하며, iOS 기기에서도 홈 화면에 추가할 수 있습니다.

---

## 📝 Development Notes

### Recent Updates

- **v1.4.0** (2024-12-04): UX Improvements (Telemetry Guide, Pagination, Data Persistence relocation)
- **v1.3.0** (2024-12-04): Custom color theme with Tailwind variables
- **v1.2.0** (2024-12-04): Log edit feature with audit trail
- **v1.1.0** (2024-12-03): Modular architecture refactoring
- **v1.0.0** (2024-12-03): Initial prototype

### Git Workflow

```bash
# Create feature branch
git checkout -b feat/feature-name

# Commit with semantic message
git commit -m "feat: Add new feature"

# Push and create PR
git push origin feat/feature-name
```

**Semantic Commit Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `style:` - UI/styling changes
- `docs:` - Documentation

---

## 🐛 Known Issues

None at this time. Please report issues on [GitHub Issues](https://github.com/greenkey20/inner-orbit/issues).

---

## 🚧 Roadmap

### Planned Features
- [ ] Dark mode support
- [ ] Multi-language support (EN/KO)
- [ ] Cloud sync (Firebase/Supabase)
- [ ] Export to PDF
- [ ] Advanced analytics dashboard
- [ ] Mood trend visualization
- [ ] User-selectable themes

### Nice to Have
- [ ] Edit history tracking
- [ ] Undo/Redo functionality
- [ ] Tags and categories
- [ ] Search and filter
- [ ] Reminders and notifications

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feat/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: Add amazing feature'`)
4. Push to the branch (`git push origin feat/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**greenkey20**

- GitHub: [@greenkey20](https://github.com/greenkey20)
- Repository: [inner-orbit](https://github.com/greenkey20/inner-orbit)

---

## 🙏 Acknowledgments

- Inspired by cognitive behavioral therapy (CBT) principles
- UI design influenced by cosmic/space aesthetics
- Built with modern React and Tailwind CSS best practices

---

<div align="center">

**🌌 Made with ❤️ and Gravity Assist Protocol**

*Converting emotional gravity into growth momentum since 2024*

</div>
