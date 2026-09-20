# Implementation Plan - Fix AAR Metadata Error by Upgrading SDK Version

The project is currently failing to build because `com.google.maps.android:maps-compose:8.6.0` (and likely other dependencies) requires compiling against Android API version 37, but the project is configured for API 36.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle/libs.versions.toml)
- Update `android-compileSdk` from `36` to `37`.
- Update `android-targetSdk` from `36` to `37`.

## Verification Plan

### Automated Tests
- Run `./gradlew :androidApp:assembleDebug` to verify that the AAR metadata check passes and the app builds successfully.

### Manual Verification
- Verify in Android Studio that the project syncs correctly with the new SDK version.
