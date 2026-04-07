# GoSmart Backoffice

Thymeleaf + Spring Boot admin portal with:

- JWT authentication stored in an HttpOnly cookie
- Server-side session tracking (`user_session`) with idle timeout enforcement (sliding window)
- Menu + authorization based on `application_system → function_group → function`, and a direct `user_function` assignment table with CRUDV flags

## What changed (today)

Key changes made since the morning work session:

- Reusable navbar extracted into a Thymeleaf fragment and included on protected pages
- Dashboard body split into sections and constrained to ~90% width on medium+ screens
- Authorization model changed from role tables to direct `user_function` mapping (user → function)
- Each request loads the current function permission and exposes it to controllers as `isCreate()/isEdit()/isDelete()/isView()`
- New protected test page `/testfunction` to verify permission wiring
- Added a shared service to build the common model (navbar + app version + idle timeout + selected system + menu) for any protected page
- Menu links now include `functionId` so multiple function codes can share the same `function.path` without ambiguity

## Key URLs

- `GET /login` (also `GET /`)
- `POST /login`
- `GET /dashboard?applicationSystemId=<id>` (protected)
- `GET /testfunction?applicationSystemId=<id>` (protected; displays permissions)
- `POST /logout` (also `GET /logout`)

## Authentication (JWT + DB session)

High level:

- Login creates a DB session row (`user_session`) with `session_id` (UUID), `expire_dt`, etc.
- JWT contains:
  - `sub` = user id
  - `jti` = session id
  - `exp` = expiry
- On every protected request:
  - Validate JWT + session row
  - Enforce revocation + idle timeout
  - Touch session (`expire_dt = now + idleTimeoutMinutes`)
  - Rotate JWT (refresh `exp`) and update cookie

Entry points:

- [AuthController.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/controller/AuthController.java)
- [AuthService.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/service/AuthService.java)
- [AuthInterceptor.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/interceptor/AuthInterceptor.java)
- [JwtProvider.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/security/JwtProvider.java)
- [AuthCookie.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/security/AuthCookie.java)

## Authorization + Menu model (user_function)

The menu and permissions are driven by these tables:

- `application_system`
- `function_group` (belongs to `application_system`)
- ``function`` (belongs to `function_group`)
- `user_function` (maps `user_id` → `function_id` with CRUDV flags)

Important MySQL notes:

- Table name ``function`` is a reserved word; use backticks in SQL.
- Column names `create` and `delete` are also reserved words; use backticks in SQL and JPA mappings.

Rules used by the menu builder:

- Only active rows are considered:
  - `application_system.status = 'A'`
  - `function_group.status = 'A'`
  - ``function``.`status = 'A'`
  - `user_function.status = 'A'`
- A menu item is shown only if `user_function.view = 'Y'`.
- Menu is filtered by the selected application system:
  - User selects with `?applicationSystemId=<id>` or defaults to the first accessible system.

Entry points:

- Native SQL for systems + menu rows: [UserFunctionRepository.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/repo/UserFunctionRepository.java)
- Model assembly: [MenuService.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/service/MenuService.java)

## Permission propagation (isCreate/isEdit/isDelete/isView)

For each protected request, the interceptor loads permission using this priority:

- If `functionId` query param is present (ex: `/testfunction?functionId=123`), permission is loaded by `function.function_id`.
- Otherwise, it matches `function.path` against `request.getRequestURI()`.
- If a matching function is found:
  - If `view != 'Y'`, the request is blocked with HTTP 403.
  - Otherwise, a permission object is attached to the request.
- If no matching `function.path` exists, no permission is attached (request continues).

Request attributes:

- `authUserId` (string): current user id
- `authPermission` (object): current page permission (nullable)
- `authFunctionCode` (string): resolved function code for the current request (nullable)

Types:

- [UserFunctionPermission.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/dto/UserFunctionPermission.java)
- Permission loading/enforcement: [AuthInterceptor.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/interceptor/AuthInterceptor.java)

Controller usage example:

```java
UserFunctionPermission p = (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
if (p != null && p.isCreate()) {
    // allowed to create
}
```

## Protected page model helper (Option A)

All protected pages need the same model attributes for navbar + system selection + menu:

- `userId`
- `idleTimeoutMinutes`
- `appVersion`
- `applicationSystems`
- `selectedApplicationSystemId`
- `menuGroups`

This is centralized in:

- [ProtectedPageModelService.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/service/ProtectedPageModelService.java)

Controllers using it:

- [DashboardController.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/controller/DashboardController.java)
- [TestFunctionController.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/controller/TestFunctionController.java)

## UI (Thymeleaf)

Reusable navbar fragment:

- Fragment: [nav.html](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/templates/fragments/nav.html)
- Usage:
  - `th:replace="~{fragments/nav :: topNav(${appVersion}, ${menuGroups}, ${userId}, ${selectedApplicationSystemId})}"`

Dashboard layout update:

- Main content is wrapped to ~90% width on medium+ screens (`.gs-main`).
- Application System grid is rendered from DB `application_system`.
- Template: [dashboard.html](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/templates/dashboard.html)

Test page:

- Controller: [TestFunctionController.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/web/controller/TestFunctionController.java)
- View: [testfunction.html](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/templates/testfunction.html)
- Purpose: show current request path + current page permission flags.

## Add a new protected page

Example flow for adding a new protected page `/abc`:

1) Create controller + template (include the navbar fragment).
2) Insert a ``function`` row with `path = '/abc'` and `status='A'` under the correct group.
3) Insert `user_function` row(s) for the user(s) that can access it:
   - `user_id = '<user>'`
   - `function_id = <function_id>`
   - `view = 'Y'` (required to access)
   - set `create/edit/delete` as needed
4) Navigate to `/abc` and read permission via `request.getAttribute("authPermission")`.

Notes:

- If you need multiple function codes to point to the same controller/page, keep `path` the same and rely on `functionId` in the menu link to disambiguate which function is being authorized.

## Database schema

- Install MySQL 8.0.x
- Create schema `gosmart`
- Import [gosmart.sql](file:///d:/Project/GoSmart/Github/Backoffice/gosmart.sql)

Current authorization table:

- `user_function`:
  - Unique: `(user_id, function_id)`
  - `status` should be `'A'` for active assignments
  - `view` controls menu visibility and page access

## Build and run (Windows)

This project targets Java 17 (see [pom.xml](file:///d:/Project/GoSmart/Github/Backoffice/pom.xml)).

Requirements:

- JDK 17+
- Maven 3.9.x

Commands:

```bat
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx PATH "%JAVA_HOME%\bin;%PATH%"

mvn -DskipTests package
java -jar target\backoffice-0.0.1.jar
```

Default URL: `http://localhost:8089/`

## Authentication and hashing (user_password)

Login verifies credentials against `user_password`. The hash is computed as:

1) Trim the provided `user_id` (no lowercase normalization is applied).
2) Compute `sha1(password)` in UTF-8 and base64-encode.
3) Concatenate `trimmedUserId + sha1Base64`.
4) Compute `sha256` over the concatenated bytes and base64-encode.

Helper:

- [PasswordHashUtil.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/util/PasswordHashUtil.java)

## No-cache + idle timer

- Global no-cache headers are applied by [WebConfig.java](file:///d:/Project/GoSmart/Github/Backoffice/src/main/java/com/gosmart/backoffice/config/WebConfig.java)
- Frontend idle timer:
  - [idle-timer.js](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/static/js/idle-timer.js)
  - used by [dashboard.html](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/templates/dashboard.html) and [testfunction.html](file:///d:/Project/GoSmart/Github/Backoffice/src/main/resources/templates/testfunction.html)

## Troubleshooting

- Build fails with record / text block errors:
  - Ensure Maven is using JDK 17+ (`mvn -v` should show Java 17+).
- Menu empty:
  - Check `user_function` has rows for the user with `status='A'` and `view='Y'`.
  - Check related `application_system/function_group/function` rows are `status='A'`.
- Page shows “No permission loaded”:
  - Add a ``function`` row with `path` matching the request URI (example: `/testfunction`).