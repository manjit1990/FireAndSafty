# Fix Swift Interop for Koin Initialization

The goal is to resolve the Swift compilation error `cannot find 'Koin_iosKt' in scope` by ensuring the Kotlin file naming and Swift code align correctly for framework generation.

## Proposed Changes

### [Shared Presentation]

#### [MODIFY] [Koin_ios.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/iosMain/kotlin/com/yoga/firesafety/shared/config/Koin_ios.kt)
- I will rename the current `KoinIos.kt` to `Koin_ios.kt`.
- In Kotlin Multiplatform, a file named `Koin_ios.kt` will generate a Swift class named `Koin_iosKt`. This matches the naming convention that the user is expecting in their Swift code.

### [iOS Application]

#### [MODIFY] [iOSApp.swift](file:///C:/Users/yoga/Desktop/New/FireAndSafty/iosApp/iosApp/iOSApp.swift)
- Ensure the Swift code explicitly calls `Koin_iosKt.initKoinIos()`.
- This will resolve the "cannot find" error as the generated framework will now expose the exact symbol.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:linkDebugFrameworkIosArm64` to verify that the framework is built successfully with the new naming.
- Run `./gradlew :androidApp:assembleDebug` to ensure Android compatibility.

### Manual Verification
- Once pushed to CI, the iOS Swift compilation should now pass.
- Verify that the "More" screen and other features still work correctly on Android.
