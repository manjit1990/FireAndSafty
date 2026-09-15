# Implementation Plan - Firebase Migration (Demo Optimization)

This plan outlines the complete migration from the custom Spring Boot backend to **Google Firebase**. This will provide a lightning-fast demo experience with real-time updates and zero server sleep time.

## User Action Required (CRITICAL)

To proceed, you must set up the Firebase project and provide the configuration. Please follow these steps:

1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Create a new project named **FireSafety**.
3.  Add an **Android App** to the project:
    *   **Package Name**: `com.learningapp.firesafetyservicemanagement`
4.  Download the `google-services.json` file.
5.  **Enable Services**:
    *   **Authentication**: Enable "Email/Password" sign-in provider.
    *   **Firestore Database**: Create a database in "Test Mode" (so we can read/write easily for the demo).
6.  **UPLOAD**: Please upload the `google-services.json` content here or tell me once you have placed it in the `androidApp/` folder.

---

## Proposed Changes

### 1. Dependency Updates
#### [MODIFY] [shared/build.gradle.kts](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/build.gradle.kts) & [libs.versions.toml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle/libs.versions.toml)
- Add Firebase KMP SDKs:
    *   `dev.gitlive:firebase-auth`
    *   `dev.gitlive:firebase-firestore`

### 2. Authentication Migration
#### [MODIFY] [LoginViewModel.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/LoginViewModel.kt) & [SignupViewModel.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/SignupViewModel.kt)
- Remove `FireSafetyApi` calls.
- Implement Firebase Auth for sign-in and registration.
- Store additional user metadata (Full Name, Role) in a Firestore `users` collection.

### 3. Data Layer Migration
#### [MODIFY] [WorkOrderRepositoryImpl.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/repository/WorkOrderRepositoryImpl.kt)
- Replace Ktor/SQLDelight synchronization logic with direct Firestore listeners.
- This will enable **real-time updates**: as soon as you assign a task in the Admin Portal, it will appear on the Technician's phone without refreshing.

### 4. Admin Portal Logic
#### [MODIFY] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- Update to write assignments directly to Firestore.

## Verification Plan

### Manual Verification
1.  **Authentication**: Verify you can sign up and log in using Firebase.
2.  **Real-time Sync**: Open the app on two devices. Create an order on one and verify it appears instantly on the other.
3.  **Stability**: Confirm there are no more "Socket timeout" or "Port binding" errors.
