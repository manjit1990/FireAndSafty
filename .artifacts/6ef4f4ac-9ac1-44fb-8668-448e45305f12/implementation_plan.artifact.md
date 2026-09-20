# Fix Koin Interop: Explicitly Exporting `initKoinIos` to Swift

The goal is to resolve the Xcode error `type 'Koin_iosKt' has no member 'initKoinIos'` by ensuring the Kotlin symbol is exported with the exact name and visibility expected by the Swift compiler.

## Research Findings

1.  **Symbol Mismatch**: Swift recognizes `Koin_iosKt` as a type, but doesn't see `initKoinIos` as a member. This happens when the Kotlin compiler mangles the function name (often adding suffixes like `_` or using parts of the package name) or hides it due to naming conflicts (e.g., functions starting with `init` being treated as Swift initializers).
2.  **Package Flattening**: Kotlin flattens packages in the generated Objective-C header. If multiple files in different packages have similar names, they may be prefixed.

## Proposed Changes

### [Shared Module]

#### [MODIFY] [Koin_ios.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/iosMain/kotlin/com/yoga/firesafety/shared/config/Koin_ios.kt)

- I will use `@file:ObjCName` to force the generated class name to be exactly `Koin_iosKt`.
- I will use `@ObjCName` with `exact = true` on the function to force the name to be exactly `initKoinIos` in the Objective-C/Swift bridge, bypassing any "initializer" detection or mangling.

```kotlin
@file:OptIn(kotlin.experimental.ExperimentalObjCName::class)
@file:ObjCName("Koin_iosKt", exact = true)
package com.yoga.firesafety.shared.config

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName
...

@ObjCName("initKoinIos", exact = true)
fun initKoinIos() {
    ...
}
```

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:linkDebugFrameworkIosArm64`.
- If successful, it confirms the framework is compilable.

### Manual Verification
- Once pushed, rebuild the Xcode project. The error `type 'Koin_iosKt' has no member 'initKoinIos'` should be resolved.
