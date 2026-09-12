# Implementation Plan - Backend Stability & Performance

This plan addresses the "Exited with status 1" error by optimizing the backend for Render's 512MB RAM environment and ensuring robust database initialization.

## Proposed Changes

### [backend] component

#### [MODIFY] [application.yml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/resources/application.yml)
- Limit the database connection pool (HikariCP) to reduce memory overhead.
- Ensure the app binds correctly to Render's dynamic port.

#### [MODIFY] [Dockerfile](file:///C:/Users/yoga/Desktop/New/FireAndSafty/Dockerfile)
- Add Java memory limit flags (`-Xmx384m`, `-Xms256m`) to the startup command. This prevents the JVM from exceeding Render's 512MB limit.
- Optimize the build process by cleaning before building.

#### [MODIFY] [V7__force_demo_users.sql](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/resources/db/migration/V7__force_demo_users.sql)
- Simplify the migration to ensure it's idempotent (safe to run multiple times).

## Verification Plan

### Manual Verification
1. **Apply Changes**: I will update the files.
2. **Push to GitHub**: You must push to GitHub.
3. **Monitor Render**: Watch the logs. With memory limits and connection pool limits, the app should start reliably within 1-2 minutes.
4. **Login**: Verify the "ADMIN" login works.
