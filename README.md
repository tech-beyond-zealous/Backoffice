# GoSmart BackOffice – Auth (JWT + Idle Timeout) + RBAC Menu

This project implements a Thymeleaf + Spring Boot admin portal with JWT-based authentication and a server-enforced idle timeout. It uses a DB-backed session table to track last activity and expire sessions after a configurable period of inactivity. The frontend also includes a configurable idle timer to proactively redirect users when idle.

## Overview

- Stateless JWT issued at login, stored in an HttpOnly cookie.
- Each login creates a server-side session row with a stable `session_id` (UUID). The JWT includes this value in the `jti` claim.
- On every protected request within protected paths:
  - Validate the JWT signature and `exp`.
  - Load the session by `session_id`, enforce idle timeout and revocation.
  - Update `last_activity_dt` and reset `expire_dt` to now + configured minutes.
  - Rotate the JWT so `exp` always reflects the new idle window.
- Frontend idle timer proactively redirects to login after configured minutes of inactivity; it does not call backend logout (prevents cross-tab logouts).
- Protected responses are served with no-cache headers.

## Recent Enhancements

### RBAC-driven dashboard menu

The dashboard top navigation menu is built dynamically from RBAC tables (filtered by the logged-in user).

- Groups come from `function_group`
- Items come from `function`
- Access is resolved by joining: `user_role` → `role_function` → `function` → `function_group` (via `user_role.role_function_id`)
- `user_role` grants access by linking a user directly to `role_function` rows (not by linking to a separate `role` table)
- Only active rows (`status = 'A'`) are included
- The `function` table name is a reserved word in MySQL, so it is referenced as `` `function` `` in SQL and mapped accordingly in JPA.

Entry points:
- Query: [FunctionRepository.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/repo/FunctionRepository.java)
- Menu model building: [MenuService.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/service/MenuService.java)
- Controller: [DashboardController.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/controller/DashboardController.java)
- Rendering: [dashboard.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/dashboard.html)

### Full-page loading overlays (login, dashboard, logout)

- Login: shows overlay after client-side validation passes and form is submitted.
- Dashboard: shows overlay immediately and hides it only on `window.load` (after the page and assets finish loading).
- Logout: re-shows overlay after submit and disables the button to prevent double-submit.

Files:
- [login.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/login.html)
- [dashboard.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/dashboard.html)

### Single active login per user (multi-login kick)

Policy: when a user logs in from Browser B, any existing active session(s) for that `user_id` are revoked. Browser A is redirected to the login page on its next request.

- Login revokes existing sessions for that user with `revoke_reason = 'multi_login'`: [AuthService.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/service/AuthService.java)
- Interceptor redirects revoked sessions to `/login?reason=multi_login`: [AuthInterceptor.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/interceptor/AuthInterceptor.java)
- Login page displays the message: “Multiple login detected. Your session is invalidated.”: [login.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/login.html)

### Backend-controlled login alert messages

The login page no longer contains a list of every possible alert message. The backend sets exactly what to display via:

- `alertMessage` (string) and optional `alertType` (Bootstrap types like `warning`, `danger`)

Entry point: [AuthController.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/controller/AuthController.java)

## Key Paths

- Login page: `GET /login`
- Submit login: `POST /login`
- Dashboard (protected): `GET /dashboard`
- Logout: `POST /logout` (also `GET /logout` supported)

## End-to-end flow (Layman term): login → dashboard

This is what happens from the moment a user opens the login page until the dashboard is shown.

1) User opens the login page
- Browser requests `GET /login`
- Backend returns `login.html` (a normal HTML page with a login form)

2) User submits username + password
- Browser sends `POST /login` with the form values
- Backend validates the inputs (blank user id / password will be rejected)

3) Backend checks credentials and creates a server session record
- Backend loads the user’s password record from DB (`user_password`)
- Backend hashes the provided password and compares to the stored hash
- If password is correct:
  - Backend revokes any existing active sessions for the same user (single-login policy)
  - Backend creates a new session row in DB (`user_session`) with:
    - `session_id` (UUID)
    - `user_id`
    - timestamps (`create_dt`, `last_activity_dt`, `expire_dt`)
    - request info (`ip_address`, `user_agent`)

4) Backend issues a JWT and stores it in a cookie
- Backend creates a signed JWT that contains:
  - `sub` = user id
  - `jti` = session id (so we can find the DB session row)
  - `exp` = expiry time
- Backend sends the JWT back to the browser in an HttpOnly cookie (example name: `GS_AT`)

5) Browser navigates to dashboard
- Backend responds to the login request with `redirect:/dashboard`
- Browser follows the redirect and requests `GET /dashboard`

6) Interceptor protects the dashboard (this runs before the dashboard controller)
- `AuthInterceptor` runs for `/dashboard` routes:
  - Reads the JWT from the auth cookie
  - Verifies JWT signature and checks `exp`
  - Loads the session row from DB using `session_id` (JWT `jti`)
  - Blocks the request if the session is revoked / expired (redirects to login with a reason)
  - If valid, it “touches” the session:
    - updates `last_activity_dt`
    - pushes `expire_dt` forward (idle timeout sliding window)
  - Issues a refreshed JWT (new `exp`) and updates the cookie
  - Stores `authUserId` into the request for controllers to use

7) Dashboard controller builds the screen and returns HTML
- `DashboardController` reads `authUserId` from the request
- It calls `MenuService` to build authorized menu items:
  - `MenuService` loads menu rows via RBAC joins (based on current user id)
- Backend returns `dashboard.html` populated with the menu + user id

8) Browser finishes loading the page
- Browser loads the dashboard HTML + CSS/JS
- The dashboard loading overlay is hidden only after `window.load` (so the user sees the fully loaded page)

## Login Validation

Login uses manual validation (no `@Valid` / `BindingResult`):

- Backend (authoritative):
  - [AuthController](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/controller/AuthController.java) checks for blank `userId` and `password` before doing auth work.
  - It sets per-field model attributes: `userIdError` and `passwordError`.
- Frontend (UX):
  - [login.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/login.html) has a small inline script that prevents submit when fields are blank and shows the same per-field messages:
    - `User Id is required`
    - `Password is required`
  - Backend validation still runs even if JS is disabled.

## Source Layout

This project uses a layered package structure for clarity as the codebase grows:

- `domain/`: JPA entities (DB persistence model)
- `repo/`: Spring Data repositories (DB access)
- `service/`: business logic and orchestration (calls repos)
- `security/`: auth/security primitives (JWT issuing/parsing, auth cookie handling)
- `util/`: pure helpers (no Spring wiring)
- `web/`: HTTP layer (controllers/interceptors)
- `dto/`: shared DTOs used by the web layer (and potentially by services later)

- App entry: [BackOfficeApplication.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/BackOfficeApplication.java)
- Config:
  - [application.yml](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/application.yml)
  - [AuthProperties.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/config/AuthProperties.java)
  - [WebConfig.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/config/WebConfig.java)
- Security:
  - [JwtProvider.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/security/JwtProvider.java)
  - [AuthCookie.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/security/AuthCookie.java)
  - [AuthTokenClaims.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/security/AuthTokenClaims.java)
- Services:
  - [AuthService.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/service/AuthService.java)
  - [MenuService.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/service/MenuService.java)
- Util:
  - [PasswordHashUtil.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/util/PasswordHashUtil.java)
- Web:
  - Controllers:
    - [AuthController.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/controller/AuthController.java)
    - [DashboardController.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/controller/DashboardController.java)
  - Interceptors:
    - [AuthInterceptor.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/web/interceptor/AuthInterceptor.java)
- DTO:
  - [LoginForm.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/dto/LoginForm.java)
- Views and assets:
  - [login.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/login.html)
  - [dashboard.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/dashboard.html)
  - [idle-timer.js](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/static/js/idle-timer.js)

## Database Schema

Please install MySQL community version 8.0.45 in your local workstation.

Create gosmart database.

Don't use root for this project. Create a new user gosmartdev with password Just13tm31n!2 and grant it all privileges on gosmart database.

Please refer to gosmart.sql for the database schema and insert initial data. Run gosmart.sql in MySQL client.


### RBAC tables (menu authorization)

Tables used to build the dashboard menu:

- `function_group`
- ``function`` (reserved word; use backticks in MySQL)
- `user_role`
- `role_function`

High-level relationships:
- `function_group` includes a unique `group_code` for stable identification
- `role_function` stores role info (`role_code`, `role_name`) and maps each role to functions (one row per `(function_id, role_code)`, with a surrogate key `role_function_id`)
- `user_role` maps a user to permitted `role_function` rows (one row per `(role_function_id, user_id)`, with a surrogate key `user_role_id`)
- A function belongs to one function group (`function.group_id`)

Menu filtering:
- Only rows with `status = 'A'` are considered active and appear in the menu.

## Configuration

All configuration lives in [application.yml](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/application.yml) and can be overridden via environment variables.

- Database:
  - `spring.datasource.url` (env: `DB_URL`) default `jdbc:mysql://localhost:3306/gosmart?...`
  - `spring.datasource.username` (env: `DB_USERNAME`)
  - `spring.datasource.password` (env: `DB_PASSWORD`)
- Auth:
  - `app.auth.cookie-name` default `GS_AT`
  - `app.auth.cookie-path` default `/`
  - `app.auth.cookie-secure` (env: `AUTH_COOKIE_SECURE`) default `false` for dev; set to `true` in production over HTTPS
  - `app.auth.cookie-same-site` default `Lax`
  - `app.auth.idle-timeout-minutes` default `30`
  - `app.auth.jwt-secret` (env: `JWT_SECRET`) must be a long random string suitable for HMAC (32+ bytes recommended)

## Build and Run

Requirements:

- Java 25 at `C:\Program Files\Java\jdk-25` (or set `JAVA_HOME` accordingly)
- Maven 3.9.x

Commands:

```
setx JAVA_HOME "C:\Program Files\Java\jdk-25"
setx PATH "%JAVA_HOME%\bin;%PATH%"

mvn -DskipTests package
java -jar target\backoffice-0.0.1.jar
```

Application runs on `http://localhost:8089/` by default.

## Authentication and Hashing

Login verifies credentials against `user_password`. The hash is computed as:

1. Trim the provided `user_id` (no lowercase normalization is applied).
2. Compute `sha1(password)` in UTF-8 and base64-encode.
3. Concatenate `trimmedUserId + sha1Base64`.
4. Compute `sha256` over the concatenated bytes and base64-encode.

Use [PasswordHashUtil.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/util/PasswordHashUtil.java) to generate `hashed_pwd` values when seeding users.

## Request Lifecycle

- Client sends request to protected path (e.g., `/dashboard`) with JWT cookie.
- Interceptor:
  - Reads JWT from cookie.
  - Validates signature and checks `exp`.
  - Loads `user_session` by `session_id` (JWT `jti`) and enforces idle and revoked checks.
  - Calls `touchSession`: updates `last_activity_dt` and sets `expire_dt = now + configured minutes`.
  - Issues a new JWT with refreshed `exp` and sets it in the cookie.
- If anything fails, redirect to `/login` with `reason` query for a user-friendly alert.

## Frontend Idle Timer

The script [idle-timer.js](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/static/js/idle-timer.js) runs on the dashboard and listens for activity. The configured idle minutes are injected by the server as `window.GS_IDLE_TIMEOUT_MINUTES` in [dashboard.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/dashboard.html).

- Tracks last activity timestamp using `localStorage` (with a safe in-memory fallback) and checks periodically for expiry.
- Resets on user actions: `click`, `keydown`, `scroll`, `mousemove`, `touchstart`.
- On expiry, redirects to `/login?reason=idle_timeout` without calling backend logout, avoiding cross-tab forced logout.
- Adds console diagnostics; logs are prefixed with `[GoSmart][IdleTimer]`. You can disable logs by setting `window.GS_IDLE_DEBUG = false` in DevTools.

The backend also enforces idle timeout even if the frontend timer is bypassed.

## No-Cache

The app sets `Cache-Control: no-store, no-cache, must-revalidate`, `Pragma: no-cache`, and `Expires: 0` globally (for controller responses) to prevent back-button access after logout and to avoid browsers caching auth-related pages.

Static JS cache control:
- [WebConfig](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/config/WebConfig.java) registers a global no-cache interceptor (excludes `/js/**`) and sets `no-store` for `/js/**`.
- [dashboard.html](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/resources/templates/dashboard.html) loads the idle timer with a cache-busting query param: `th:src="@{/js/idle-timer.js(v=${idleTimeoutMinutes})}"`.

## Extending Protected Areas

Currently, `/dashboard` and `/dashboard/**` are protected in [WebConfig.java](file:///d:/Project/GoSmart/Source%20Code/BackOffice/src/main/java/com/gosmart/backoffice/config/WebConfig.java). To add more protected areas, include additional path patterns in the interceptor registry.

## Operational Notes

- `cookie-secure` should be `true` in production.
- `jwt-secret` must be long and random. Rotate carefully if needed.
- `session_id` is an opaque UUID stored server-side and placed in JWT `jti`. Do not store raw JWTs in the DB.
- Logout sets `revoke_dt` so any subsequent requests are rejected until a new login.
- `user_session` timestamps are stored as `DATETIME` using the server’s local +08:00 time via application logic (`LocalDateTime` with `ZoneOffset.ofHours(8)`).

## Troubleshooting

- Build errors:
  - Ensure `JAVA_HOME` points to JDK 25 and `mvn -v` shows the correct Java.
- DB connection:
  - Update `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`; verify the `gosmart` schema and required tables exist.
- Invalid token errors:
  - Verify the `JWT_SECRET` is the same across instances and is sufficiently long.
- Can’t access dashboard after login:
  - Check `user_session` rows are created. Ensure the cookie is being set (browser dev tools > Cookies).
- Frontend idle timeout doesn’t trigger:
  - Open DevTools Console and watch `[GoSmart][IdleTimer]` logs to ensure the timer reads the correct minutes and isn’t being reset by activity noise (e.g., constant mouse move).
  - Confirm `dashboard.html` injects `window.GS_IDLE_TIMEOUT_MINUTES` and the script loads with the `v` query param.
  - Ensure only one tab stays active if you want the session to expire; other tabs may keep you active.
