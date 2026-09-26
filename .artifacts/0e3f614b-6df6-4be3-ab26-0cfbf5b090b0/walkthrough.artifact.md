# Walkthrough - iOS Build Dependency Cleanup

Cleaned up `shared/build.gradle.kts` to resolve iOS build conflicts and compiler resolution errors:
1. Removed `androidx.lifecycle.viewmodelSavedstate` and `androidx.savedstate` from `iosMain.dependencies`.
2. Removed hardcoded `androidx.savedstate:savedstate:1.2.1` from `commonMain.dependencies`.

## Changes

### Build Configuration

#### [build.gradle.kts](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/build.gradle.kts)
- Cleaned up redundant dependencies in `iosMain` and `commonMain`.

## Verification Results

### Automated Tests
- Executed `assembleDebug` gradle build successfully with 0 compilation or syntax errors.
