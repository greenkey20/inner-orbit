# 자동 배포 설정 가이드 (Phase 3)

## 📋 개요

이 가이드는 GitHub Actions를 통한 자동 배포를 설정하는 방법을 안내합니다.

**작동 방식**: `main` 브랜치에 Push → GitHub Actions가 자동으로 서버에 SSH 접속 → `git pull` + `docker compose up -d --build` 실행

---

## 🔐 Step 1: SSH 키 생성 (서버에서)

### 1-1. 서버에 SSH 접속
```bash
ssh your-username@your-server-ip
```

### 1-2. SSH 키 페어 생성
```bash
# GitHub Actions 전용 SSH 키 생성
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/github_actions_deploy

# 파일 2개 생성됨:
# - github_actions_deploy (Private Key) - GitHub Secrets에 저장
# - github_actions_deploy.pub (Public Key) - 서버에 등록
```

### 1-3. Public Key를 authorized_keys에 추가
```bash
cat ~/.ssh/github_actions_deploy.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

### 1-4. Private Key 복사 (GitHub Secrets용)
```bash
cat ~/.ssh/github_actions_deploy
```

**출력된 전체 내용을 복사** (-----BEGIN OPENSSH PRIVATE KEY----- 부터 -----END OPENSSH PRIVATE KEY----- 까지 전부)

---

## 🔑 Step 2: GitHub Secrets 설정

### 2-1. GitHub 저장소로 이동
1. https://github.com/greenkey20/inner-orbit 접속
2. **Settings** 탭 클릭
3. 왼쪽 메뉴에서 **Secrets and variables** → **Actions** 클릭

### 2-2. Secrets 추가

**New repository secret** 버튼을 클릭하고 다음 4개 Secret을 추가:

#### ① SERVER_HOST
```
Name: SERVER_HOST
Secret: your-server-ip-address
예: 123.456.789.012 또는 your-domain.com
```

#### ② SERVER_USER
```
Name: SERVER_USER
Secret: your-ssh-username
예: ubuntu, greenkey20 등
```

#### ③ SERVER_PORT
```
Name: SERVER_PORT
Secret: 22
(기본 SSH 포트. 변경했다면 해당 포트 번호)
```

#### ④ SSH_PRIVATE_KEY
```
Name: SSH_PRIVATE_KEY
Secret: (Step 1-4에서 복사한 Private Key 전체 내용)

예:
-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtZW
QyNTUxOQAAACBK...
(여러 줄)
...AAAAA==
-----END OPENSSH PRIVATE KEY-----
```

#### ⑤ PROJECT_PATH
```
Name: PROJECT_PATH
Secret: /home/your-username/projects/inner-orbit-system
(서버에서 프로젝트가 위치한 절대 경로)
```

---

## 🧪 Step 3: 로컬에서 SSH 접속 테스트

배포 전에 SSH 접속이 잘 되는지 확인:

```bash
# Private Key로 SSH 접속 테스트
ssh -i ~/.ssh/github_actions_deploy your-username@your-server-ip

# 접속 성공하면 OK!
```

---

## 🚀 Step 4: 자동 배포 활성화

### 4-1. 워크플로우 커밋 & Push
```bash
git add .github/workflows/deploy.yml
git commit -m "ci: Add automatic deployment workflow (Phase 3)"
git push origin main
```

### 4-2. 첫 배포 실행 확인
1. https://github.com/greenkey20/inner-orbit/actions 접속
2. **Deploy to Production** 워크플로우 실행 확인
3. 로그에서 배포 과정 실시간 확인

---

## ✅ Step 5: 배포 확인

### 5-1. GitHub Actions 로그 확인
```
✓ Deploy via SSH
  - Starting deployment
  - Pulling latest code
  - Rebuilding Docker containers
  - Checking container status
  - Deployment completed
```

### 5-2. 서버에서 직접 확인
```bash
# 서버 접속
ssh your-username@your-server-ip

# Docker 컨테이너 상태 확인
sudo docker compose ps

# 로그 확인
sudo docker compose logs --tail=50 backend
sudo docker compose logs --tail=50 frontend
```

### 5-3. 브라우저에서 확인
```
http://your-server-ip
또는
http://your-domain.com
```

---

## 🔧 트러블슈팅

### 문제 1: Permission denied (publickey)
**원인**: SSH 키가 제대로 설정되지 않음

**해결**:
```bash
# 서버에서
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh

# GitHub Secret SSH_PRIVATE_KEY가 정확한지 확인
# (공백이나 줄바꿈 누락 없이 전체 복사)
```

### 문제 2: docker: command not found
**원인**: GitHub Actions가 `docker compose` 대신 `docker-compose` 사용 중

**해결**: 서버에 Docker Compose V2 설치 확인
```bash
docker compose version
# Docker Compose version v2.x.x
```

### 문제 3: Permission denied (docker)
**원인**: 사용자가 docker 그룹에 속하지 않음

**해결**:
```bash
# 서버에서
sudo usermod -aG docker $USER
# 로그아웃 후 재로그인 필요
```

또는 deploy.yml에서 `sudo` 사용 (현재 설정):
```yaml
sudo docker compose up -d --build
```

### 문제 4: git pull fails
**원인**: 서버의 Git 저장소 상태 이상

**해결**:
```bash
# 서버에서
cd /path/to/inner-orbit-system
git status
git stash  # 로컬 변경사항이 있다면
git pull origin main
```

---

## 📊 배포 프로세스 요약

```
개발자가 코드 작성
  ↓
git commit & push
  ↓
GitHub (main 브랜치)
  ↓
GitHub Actions 자동 실행
  ↓
[Phase 1] Backend Tests 실행 ✅
  ↓
[Phase 2] Docker Build 검증 ✅
  ↓
[Phase 3] SSH로 서버 접속
  ↓
서버에서 실행:
1. cd /path/to/project
2. git pull origin main
3. sudo docker compose up -d --build
  ↓
배포 완료! 🚀
```

---

## 🎯 완료 체크리스트

- [ ] SSH 키 생성 (서버)
- [ ] Public Key를 authorized_keys에 추가
- [ ] Private Key 복사
- [ ] GitHub Secrets 5개 추가:
  - [ ] SERVER_HOST
  - [ ] SERVER_USER
  - [ ] SERVER_PORT
  - [ ] SSH_PRIVATE_KEY
  - [ ] PROJECT_PATH
- [ ] SSH 접속 테스트 성공
- [ ] deploy.yml 커밋 & Push
- [ ] GitHub Actions에서 배포 성공 확인
- [ ] 서버에서 컨테이너 실행 확인
- [ ] 브라우저에서 서비스 접속 확인

---

## 📝 다음 단계

Phase 3 완료 후:
1. **CI/CD 개념 학습 자료** 읽기
2. 프롬프트 엔지니어링 작업 (Issue #30, #34) 진행

---

**작성일**: 2026-01-03
**Phase**: CI/CD Phase 3 - Automatic Deployment
