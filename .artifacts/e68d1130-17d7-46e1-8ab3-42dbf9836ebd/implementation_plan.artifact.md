# Implementation Plan - Seamless Demo Experience

This plan addresses the frustration of repeated "Bad credentials" errors by making the login process more robust and providing quick-access demo accounts.

## Proposed Changes

### [shared] component

#### [MODIFY] [LoginScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/LoginScreen.kt)
- **Demo Buttons**: Add two professional "Quick Login" chips/buttons below the Sign In button:
    - **Login as Admin** (Auto-fills `admin@demo.com` / `password`)
    - **Login as Technician** (Auto-fills `tech@demo.com` / `password`)
- **Clearer Errors**: Update the error display to show only the essential message (e.g., "Invalid Email or Password") instead of the full JSON technical details.

---

### [backend] component

#### [MODIFY] [GlobalExceptionHandler.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/exception/GlobalExceptionHandler.java)
- Add a specific handler for `BadCredentialsException`.
- Return an `Unauthorized (401)` status instead of `Internal Server Error (500)`.
- Provide a clean, user-friendly error message.

#### [MODIFY] [V5__demo_data.sql](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/resources/db/migration/V5__demo_data.sql)
- Ensure the demo accounts are clearly defined and persistent.

## Verification Plan

### Manual Verification
1. **Push Backend**: Deploy the updated error handling to Render.
2. **Test Quick Login**:
    - Open the app.
    - Click "Login as Admin".
    - Verify it auto-fills and logs you in instantly.
3. **Test Bad Credentials**:
    - Type a wrong password.
    - Verify the error message is now clean ("Invalid Email or Password") and doesn't show technical JSON.
