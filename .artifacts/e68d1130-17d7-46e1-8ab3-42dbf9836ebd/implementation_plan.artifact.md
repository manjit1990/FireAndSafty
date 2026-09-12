# Implementation Plan - Force Demo Credentials

The "Invalid email or password" error indicates that the demo account `admin@demo.com` either doesn't exist in your live database or has a different password. This plan will force the database to include these accounts with the correct credentials.

## Proposed Changes

### [backend] component

#### [NEW] [V7__force_demo_users.sql](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/resources/db/migration/V7__force_demo_users.sql)
- Create a new migration file that uses `ON CONFLICT` logic.
- This will either insert the demo users if they are missing or update their passwords to `password` (hashed) if they already exist.
- Accounts:
    - `admin@demo.com` (ADMIN)
    - `tech@demo.com` (TECHNICIAN)

## Verification Plan

### Manual Verification
1. **Apply Change**: I will create the SQL file.
2. **Push to GitHub**: You must push this to GitHub.
3. **Wait for Render**: Wait for the "Live" status.
4. **Login**: Click the **ADMIN** button on the Login screen. It should now work 100%.
