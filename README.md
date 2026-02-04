# HiddenGrowth

AI 기반 성장 분석 플랫폼: 경험 입력 → 스킬/강점 분석 → 대시보드 → 포트폴리오 PDF 생성

## Monorepo Structure
- `frontend/` : React + TypeScript + Vite
- `backend/`  : Spring Boot 3.x + Java 17
- `ai/`       : FastAPI (분석 엔진)
- `infra/`    : Docker Compose (MySQL 등)
- `shared/`   : OpenAPI/계약/공용 assets
- `docs/`     : 기획/결정/명세
- `scripts/`  : 로컬 개발 편의 스크립트

## Quick Start (Local)

### Prerequisites
- Docker Desktop
- Java 17
- Node.js LTS
- Python 3.11+

### 1) Infra (MySQL)
```bash
cd infra
docker compose up -d
```

### 2) Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
http://localhost:8080

### 3) AI (FastAPI)
```bash
cd ai
python -m venv .venv
# Windows
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8001
```
http://localhost:8001/health

### 4) Frontend (Vite)
```bash
cd frontend
npm install
npm run dev
```
http://localhost:5173

### Environment Variables
예시 파일을 복사해서 사용:
- Backend: backend/.env.example → backend/.env

- AI: ai/.env.example → ai/.env

- Frontend: frontend/.env.example → frontend/.env

### API (MVP 예정)
- POST /chat : 경험 입력 저장
- POST /analysis/finalize : 분석 Job 생성
- GET /analysis/jobs/{jobId} : Job 상태 조회
- GET /analysis/jobs/{jobId}/result : 분석 결과 조회

### Branch & PR Convention
- Branch: feature/<scope>-<short>
  - 예: feature/job-table, feature/analysis-rules, feature/auth-jwt
- PR 제목 Prefix: [FE], [BE], [AI], [INFRA]

### Notes
DB는 기본적으로 Docker MySQL(infra/docker-compose.yml)을 사용한다.
shared/에는 FE/BE/AI 간 계약(JSON 스키마, OpenAPI 스냅샷 등)을 보관한다.