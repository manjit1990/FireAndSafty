# Fix iOS KLIB Resolution for SavedState and Lifecycle

The iOS build on CI is failing because the KLIB resolver cannot find `org.jetbrains.androidx.savedstate:savedstate` and `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-savedstate` during the Firebase cinterop tasks. This typically happens when transitive dependencies are not fully resolved for native targets.

## Proposed Changes

### [Dependency Configuration]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle/libs.versions.toml)

- Update `androidx-lifecycle` from `2.11.0-beta01` to `2.11.0` (Stable) to ensure better compatibility.
- Add `androidx-savedstate = "1.4.0"` version.
- Add explicit library definitions for:
    - `androidx-savedstate` (`org.jetbrains.androidx.savedstate:savedstate`)
    - `androidx-lifecycle-viewmodelSavedstate` (`org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-savedstate`)

#### [MODIFY] [shared/build.gradle.kts](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/build.gradle.kts)

- Explicitly add the following to `commonMain.dependencies`:
    - `implementation(libs.androidx.savedstate)`
    - `implementation(libs.androidx.lifecycle.viewmodelSavedstate)`

Explicitly declaring these in `commonMain` ensures that the Kotlin Multiplatform resolver downloads and caches the necessary KLIBs for all targets (including iOS), which resolves the "Could not find" errors during the cinterop phase.

## Verification Plan

### Automated Tests
- Run Gradle Sync to ensure the new dependencies are resolved correctly on Windows.
- Since I cannot run the iOS linker locally, the primary verification will be the GitHub CI build.
- The command to run on CI remains: `./gradlew :shared:linkPodDebugFrameworkIosArm64 --stacktrace`.

### Manual Verification
- Verify that the Android app still builds and runs correctly (`./gradlew :androidApp:assembleDebug`).
