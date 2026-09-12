# Implementation Plan - Admin Names & Premium Dark UI

This plan aims to personalize the "Schedule Task" screen by showing the Admin's name and completely overhaul the app's visual design with a premium Dark Theme and refined UI components.

## Proposed Changes

### [backend] component

#### [MODIFY] [AuthenticationResponse.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/dto/AuthenticationResponse.java)
- Add `firstName` and `lastName` fields to the response object.

#### [MODIFY] [AuthenticationService.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/AuthenticationService.java)
- Populate `firstName` and `lastName` in the `AuthenticationResponse` during registration and login.

---

### [shared] component (Data & Logic)

#### [MODIFY] [AuthModels.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/dto/AuthModels.kt)
- Add `firstName` and `lastName` to the `AuthenticationResponse` Kotlin data class.

#### [MODIFY] [FireSafetyDatabase.sq](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/sqldelight/com/yoga/firesafety/shared/db/FireSafetyDatabase.sq)
- Update `SessionEntity` to store `firstName` and `lastName`.
- Update `saveSession` query.

#### [MODIFY] [SessionRepository.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/domain/repository/SessionRepository.kt) & [SessionRepositoryImpl.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/repository/SessionRepositoryImpl.kt)
- Update `UserSession` model and repository methods to include names.

#### [MODIFY] ViewModels (`LoginViewModel`, `SignupViewModel`, `MainViewModel`)
- Update session management logic to handle user names.

---

### [shared] component (Premium UI & Dark Theme)

#### [MODIFY] [DesignSystem.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/theme/DesignSystem.kt)
- Define a comprehensive `DarkColorScheme` using a sleek "Deep Midnight" palette.
- Refine `AppColors` for better contrast in both themes.

#### [MODIFY] [FireSafetyTheme.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/theme/FireSafetyTheme.kt)
- Enable theme switching based on the `darkTheme` parameter.

#### [MODIFY] Screen Refinement (All Screens)
- **Global**: Replace hardcoded `Color.White`, `Color.Black`, etc., with `MaterialTheme.colorScheme` tokens.
- **Login/Signup**: Improve the gradient and card styling for a "premium" feel in dark mode.
- **Dashboard**: Redesign work order cards with better elevation, padding, and status indicators.
- **Schedule Task**:
    - Display **"Assigned by: [First Name] [Last Name]"** at the top.
    - Polish the date and time picker cards.

## Verification Plan

### Manual Verification
1. **Push Backend**: Deploy the name support to Render.
2. **Login/Signup**: Verify that your name is correctly saved after logging in.
3. **Toggle Theme**: Switch the phone to Dark Mode.
4. **Visual Inspection**:
    - Check the Login screen for "premium" dark aesthetics.
    - Verify that the Admin Dashboard looks sleek and readable.
    - Confirm the "Schedule Task" screen shows your **Name** instead of your email.
    - Check that the Start/End times look great in the new UI.
