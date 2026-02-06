# Ticket System

A full-stack ticket management platform with role-based access, JWT authentication, and automated test coverage across frontend and backend layers.

## What This Project Does

- Auth flow with sign up, login, refresh, and logout.
- User ticket workflow to create and view personal tickets.
- Admin workflow to view all tickets and update ticket status.
- OpenAPI documentation support via SpringDoc.
- Containerized local/prod deployment with Docker Compose.

## Architecture

```text
frontend (React + Vite + RTK Query)
        |
        | /api proxy (Vite dev server)
        v
backend (Spring Boot + Security + JPA)
        |
        v
postgres (primary datastore)
```

## Tech Stack

| Layer | Tech |
| --- | --- |
| Frontend | React 18, TypeScript, Redux Toolkit + RTK Query, React Router, Vite, Tailwind CSS |
| Frontend Testing | Jest, React Testing Library, Playwright |
| Backend | Java 21, Spring Boot 4, Spring Security, Spring Data JPA, MapStruct, Lombok |
| Backend Testing | JUnit 5, Spring Boot Test, Cucumber, MockMvc, JaCoCo, H2 |
| API Docs | SpringDoc OpenAPI + Swagger UI |
| Infra | Docker, Docker Compose, Nginx, PostgreSQL 16 |
| Package/Build Tools | pnpm, Maven Wrapper (`./mvnw`) |

## Repository Layout

```text
.
├── frontend/   # React app + frontend tests (unit + Playwright)
├── backend/    # Spring Boot API + unit/integration/cucumber tests
└── infra/      # Docker Compose + Nginx config + local certs
```

## Getting Started (Local Dev)

### Prerequisites

- Node.js 20+ and `pnpm`
- Java 21
- Docker (recommended for PostgreSQL bootstrapping)

### 1) Start PostgreSQL

```bash
cd infra
docker compose up -d postgres
```

### 2) Start Backend

Set required environment variables (JWT and admin bootstrap):

```bash
export JWT_SECRET='replace-with-a-secure-secret'
export ADMIN_EMAIL='admin@example.com'
export ADMIN_PASSWORD='Admin123!'
```

Run API:

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`.

### 3) Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend runs on `http://localhost:5173` and proxies `/api` to `http://localhost:8080` using `VITE_API_TARGET`.

## Run Full Stack with Docker

```bash
cd infra
docker compose up --build
```

Default ingress:

- HTTP: `http://localhost:8080`
- HTTPS: `https://localhost:8443`

## API Surface (Current)

- `POST /auth/signup`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /tickets`
- `POST /tickets`
- `GET /admin/tickets` (admin)
- `PUT /admin/tickets/{ticketId}` (admin)

Swagger UI is available at `/swagger-ui.html` when backend is running.

## Test Strategy

### Frontend

- One-time browser install for Playwright:

```bash
cd frontend
pnpm exec playwright install
```

- Unit/integration tests:

```bash
cd frontend
pnpm test
```

- Coverage:

```bash
cd frontend
pnpm run test:coverage
```

- E2E smoke suite (Playwright):

```bash
cd frontend
pnpm run test:smoke
```

- Full E2E suite:

```bash
cd frontend
pnpm run test:e2e
```

- Playwright UI mode:

```bash
cd frontend
pnpm run test:e2e:ui
```

Note: In UI mode, the Playwright runner window controls execution. The browser window can appear idle until a test is selected or rerun.

If `4173` is occupied locally, override Playwright server port:

```bash
cd frontend
PLAYWRIGHT_PORT=4273 pnpm run test:e2e
```

### Backend

- Full backend test suite (unit + integration + cucumber):

```bash
cd backend
./mvnw test
```

- Cucumber only:

```bash
cd backend
./mvnw -Dtest=RunCucumberTest test
```

## CI/CD and Jenkins Smoke-Gate Recommendation

For a linear main branch policy, gate merges with fast tests first and smoke tests before deployment:

1. Frontend lint + unit (`pnpm test`).
2. Backend unit/integration (`./mvnw test` or split stages).
3. Frontend smoke (`pnpm run test:smoke`).
4. Backend cucumber smoke (`./mvnw -Dtest=RunCucumberTest test`).
5. Build/publish artifacts only if all smoke stages pass.

This gives a stable smoke contract your Jenkins pipeline can leverage before environment deploy stages.

## Future Scope

Potential high-impact extensions for this ticket system:

1. SLA and priority engine with breach alerts and escalation rules.
2. Team queues, assignment policies, and workload balancing.
3. Comments, attachments, and full activity/audit timeline per ticket.
4. Notification hub (email, Slack, webhook events, digest mode).
5. Advanced search and analytics dashboards (resolution time, backlog aging, MTTR).
6. Multi-tenant org/workspace model with RBAC per tenant.
7. Workflow automation rules (auto-routing, auto-close, status transitions).
8. AI-assisted triage for category, urgency, and response suggestions.

## License

Define your project license in this repository root when ready.
