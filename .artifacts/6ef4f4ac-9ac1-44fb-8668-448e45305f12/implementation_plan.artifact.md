# Fix iOS Linking Error: "Function getBackStackEntry is not found"

The iOS build is failing during the linking stage with a "Function not found" error related to the `androidx.navigation` library. This is a known issue when using experimental or alpha versions of libraries in Kotlin Multiplatform, where the Kotlin/Native compiler's cache becomes inconsistent or unable to resolve specific symbols.

## Proposed Changes

### [Gradle Configuration]

#### [MODIFY] [gradle.properties](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle.properties)

- Add `kotlin.native.cacheKind=none`.
- This disables the Kotlin/Native compiler cache for external libraries. While this may slightly increase build times on CI, it resolves symbol resolution issues between pre-compiled KMP libraries (like `androidx.navigation`) and your local source code.

## Verification Plan

### Automated Tests
- Run `./gradlew clean` to ensure no stale build artifacts remain.
- Run `./gradlew :shared:linkDebugFrameworkIosArm64 --stacktrace`.
- If the build passes, it confirms that the cache was indeed the cause of the linking error.

### Manual Verification
- None required as this is a build-system level fix.
