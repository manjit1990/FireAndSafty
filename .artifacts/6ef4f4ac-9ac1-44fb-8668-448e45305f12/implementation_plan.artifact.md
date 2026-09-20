# Fix iOS Build Error: Unresolved reference 'getString'

The iOS build is failing with `Unresolved reference 'getString'` in `MainApp.kt`. This is likely because the `getString` extension function for `Bundle` is not correctly resolved in the current Kotlin Native environment with the updated Navigation library.

## Proposed Changes

### [Shared Presentation]

#### [MODIFY] [MainApp.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/MainApp.kt)

- I will replace the usage of `backStackEntry.arguments?.getString("key")` with `backStackEntry.savedStateHandle.get<String>("key")`.
- `savedStateHandle` is the modern and more robust way to access navigation arguments in Jetpack Navigation, especially in a Kotlin Multiplatform context, as it avoids direct dependency on the platform-specific `Bundle` implementations for simple type retrieval.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:compileKotlinIosArm64` to verify the fix.
- Run `./gradlew :androidApp:assembleDebug` to ensure no regressions on Android.

### Manual Verification
- Verify that navigation between screens (e.g., clicking on a work order to see details) still works correctly and the IDs are passed as expected.
