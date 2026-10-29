# JobTrack
Job application tracker. Frontend: HTML/CSS/vanilla JS. Backend: Java 17, Spring Boot 3.3, Spring Data JPA, MySQL 8.4.

## Run locally
1. MySQL running. Set env vars (never commit passwords): `DB_USERNAME`, `DB_PASSWORD` (the DB `jobtrack_db` is auto-created).
2. Backend: `cd backend && mvn spring-boot:run` (or open in IntelliJ and run `JobTrackApplication`) -> http://localhost:8080
3. Frontend: open `frontend/` with VS Code Live Server (port 5500). CORS allows 5500 by default; override with `CORS_ORIGINS`.

## API
GET /api/applications?status=&jobType=&q=&sort=asc|desc | GET /api/applications/{id} | GET /api/applications/stats
POST /api/applications (201) | PUT /api/applications/{id} | DELETE /api/applications/{id} (204)
Errors: 400 (validation, bad JSON/enum), 404 (unknown id).

## Auth, pagination, history
Register/login at `POST /api/auth/register|login` (returns a JWT; send `Authorization: Bearer <token>`). Every user sees only their own applications.
`GET /api/applications` is paged: `?page=0&size=10` -> `{content, page, totalPages, totalElements}`. Each application keeps a status history and an optional `followUpDate`.
Set `JWT_SECRET` (32+ chars) in any real deployment.

## Docker
`copy .env.example to .env`, set the values, then `docker compose up --build`. App: http://localhost:5500, API: http://localhost:8080.
