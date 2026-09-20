# Fix Koin Interop for iOS framework

The goal is to ensure `initKoinIos()` is correctly exposed as a static member of `Koin_iosKt` in Swift, resolving the Xcode error `type 'Koin_iosKt' has no member 'initKoinIos'`.

## Research Findings

1.  **File Location**: `shared/src/iosMain/kotlin/com/yoga/firesafety/shared/config/Koin_ios.kt`
2.  **Package**: `com.yoga.firesafety.shared.config`
3.  **Current Status**: The Kotlin framework builds, but Swift cannot see the member. This usually happens if the function signature or naming conflicts with Swift's internal rules (like functions starting with `init` being treated as initializers).

## Proposed Changes

### [Shared Module]

#### [MODIFY] [Koin_ios.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/iosMain/kotlin/com/yoga/firesafety/shared/config/Koin_ios.kt)

- I will add `@file:ObjCName` to ensure the class name is explicitly `Koin_iosKt`.
- I will add `@ObjCName` to the function to ensure it is exported exactly as `initKoinIos` and not mangled or treated as a Swift initializer.
- I will ensure the return type is explicitly `Unit`.

```kotlin
@file:OptIn(kotlin.experimental.ExperimentalObjCName::class)
package com.yoga.firesafety.shared.config

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@ObjCName("initKoinIos")
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

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:linkDebugFrameworkIosArm64`.
- This confirms that the Kotlin code compiles and the framework can be linked.

### Manual Verification
- Once pushed, the Xcode build should now find the `initKoinIos` member on `Koin_iosKt`.
- If the error persists, I will check if the package name needs to be part of the Swift call (e.g. `Shared.ConfigKoin_iosKt.initKoinIos()`), though the error message suggests `Koin_iosKt` itself is found.
