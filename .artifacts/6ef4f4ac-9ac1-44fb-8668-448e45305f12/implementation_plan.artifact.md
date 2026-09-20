# Fix Koin Interop: "type 'Koin_iosKt' has no member 'initKoinIos'"

The iOS build is failing because Swift sees the `Koin_iosKt` class but cannot find the `initKoinIos` member. This is likely due to the function being wrapped inside an `object` in the previous fix, which changed the generated Swift API.

## Proposed Changes

### [Shared Module]

#### [MODIFY] [Koin_ios.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/config/Koin_ios.kt)
- I will move `initKoinIos` out of the `KoinIOS` object and make it a **top-level function**.
- I will ensure it returns `Unit` explicitly to avoid any issues with exporting `KoinApplication`.
- I will keep the filename as `Koin_ios.kt` so that the generated Swift class remains `Koin_iosKt`.

```kotlin
fun initKoinIos() {
    initKoin {
        modules(module {
            single { DriverFactory() }
            single<DeviceIdProvider> { IosDeviceIdProvider() }
            single<NotificationService> { IosNotificationService() }
        })
    }
}
```

### [iOS Application]

#### [MODIFY] [iOSApp.swift](file:///C:/Users/yoga/Desktop/New/FireAndSafty/iosApp/iosApp/iOSApp.swift)
- I will update the call to use the top-level bridge: `Koin_iosKt.initKoinIos()`.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:linkDebugFrameworkIosArm64`.
- If successful, this confirms the framework with the correct Swift-visible symbols has been generated.

### Manual Verification
- Rebuild the iOS project in Xcode (simulated by CI).
- The error `type 'Koin_iosKt' has no member 'initKoinIos'` should be resolved.
