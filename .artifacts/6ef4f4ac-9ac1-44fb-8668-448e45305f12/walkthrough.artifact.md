# Fix iOS Linking Error: "Function getBackStackEntry is not found"

I have resolved the iOS linking error that was preventing the creation of the shared framework.

## Changes Made

### [Gradle Configuration]

#### [gradle.properties](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle.properties)

- Added `kotlin.native.cacheKind=none`.
- **Reasoning**: This setting disables the Kotlin/Native compiler cache for external libraries. The error occurred because the linker could not find the `getBackStackEntry` function in the cached version of the `androidx.navigation` library. By disabling the cache, we force the compiler to resolve all symbols directly from the source artifacts, which is a standard workaround for symbol resolution issues in alpha/beta KMP libraries.

## Verification Results

### Automated Tests
- Ran `./gradlew clean` to reset the build state.
- Ran `./gradlew :shared:linkDebugFrameworkIosArm64 --stacktrace`.
- **Result**: The task finished **successfully**, confirming that the shared framework can now be linked correctly for iOS targets.
