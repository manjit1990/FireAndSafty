# Fix iOS Build Error: Unresolved reference 'System' (Deep Dive)

The previous attempt to fix the iOS build error using aliased imports or standard `Clock.System` calls did not resolve the issue in the CI environment. The compiler still reports `Unresolved reference 'System'` in several `commonMain` files.

## Research Findings

The error `Unresolved reference 'System'` when using `Clock.System` suggests that the `Clock` symbol is being resolved to a class/interface that does not have a `System` property, or the property itself is ambiguous to the Kotlin Native compiler. To fix this robustly across all platforms, I will use the fully qualified name `kotlinx.datetime.Clock.System` for every wall-clock time call.

## Proposed Changes

### [Shared Presentation]

I will replace all ambiguous `Clock.System` or `System.currentTimeMillis()` calls with explicit, fully qualified Kotlin Multiplatform calls.

#### [MODIFY] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- Replace all occurrences of `Clock.System.now()` with `kotlinx.datetime.Clock.System.now()`.
- Ensure imports are clean and don't clash.

#### [MODIFY] [CompleteVisitScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/CompleteVisitScreen.kt)
- Replace `Clock.System.todayIn(...)` with `kotlinx.datetime.Clock.System.todayIn(...)`.

#### [MODIFY] [WorkOrderComponents.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderComponents.kt)
- Replace all `Clock.System.now()` with `kotlinx.datetime.Clock.System.now()`.

#### [MODIFY] [WorkOrderListScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderListScreen.kt)
- Replace all `Clock.System.now()` and `Clock.System.todayIn()` with their fully qualified counterparts.

#### [MODIFY] [WorkOrder.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/domain/model/WorkOrder.kt)
- Check and fix any time-related calls.

## Verification Plan

### Automated Tests
- **Android**: Run `./gradlew :androidApp:assembleDebug` to verify local consistency.
- **iOS**: I will attempt to run `./gradlew :shared:linkDebugFrameworkIosArm64` if the environment allows, otherwise I will rely on the user to push and check CI, but I will double-check every single line of code first.

### Manual Verification
- Verify that no `java.lang.System` imports exist in `commonMain`.
- Verify that no JVM-specific time APIs are used.
