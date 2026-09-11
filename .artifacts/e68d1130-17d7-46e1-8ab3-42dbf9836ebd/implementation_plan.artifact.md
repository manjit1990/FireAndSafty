# Implementation Plan - Fix Empty User List & Auth Tokens

The "User Management" screen is empty because the app is not sending the required security token to the server. I also need to improve error reporting so you can see if something goes wrong.

## Proposed Changes

### [shared] component

#### [MODIFY] [FireSafetyDatabase.sq](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/sqldelight/com/yoga/firesafety/shared/db/FireSafetyDatabase.sq)
- Update `SessionEntity` to include a `token` field.
- Update `saveSession` query to accept and store the token.

#### [MODIFY] [SessionRepository.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/domain/repository/SessionRepository.kt) & [SessionRepositoryImpl.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/repository/SessionRepositoryImpl.kt)
- Update `UserSession` data class and repository methods to include the `token`.

#### [MODIFY] [FireSafetyApi.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)
- Update the class to store the current `token`.
- Add a helper method to set the token after login/signup.
- Include the `Authorization: Bearer <token>` header in all secured API calls (`getAllUsers`, `getWorkOrders`, etc.).

#### [MODIFY] [LoginViewModel.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/LoginViewModel.kt) & [SignupViewModel.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/SignupViewModel.kt)
- Correctly capture the `token` from the server response and save it to the session.
- Pass the token to `FireSafetyApi` immediately after a successful authentication.

#### [MODIFY] [UserManagementViewModel.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/UserManagementViewModel.kt)
- Add an `error` state.
- Update `loadUsers` to catch exceptions and update the `error` state.

#### [MODIFY] [UserManagementScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/UserManagementScreen.kt)
- Display an error message or an "Empty" state if no users are found or if the API call fails.

## Verification Plan

### Manual Verification
1. Apply the changes.
2. Run the app.
3. Log in as an Admin.
4. Navigate to "User Management".
5. Verify that the list of users is now visible (it should at least show your own account).
6. Verify that any server errors (like "Unauthorized") are now clearly displayed on the screen.
