# Fix iOS Build Error: Unresolved reference 'System'

The iOS compilation is failing because the compiler is unable to resolve the `System` property when using `Clock.System.now()` in several `commonMain` files. I will follow the user's specific instructions to resolve this by using explicit KMP-compatible time APIs.

## Proposed Changes

### [Shared Presentation]

I will search and replace all usages of `System` (where used as a clock) across the specified files. To avoid resolution issues on iOS, I will use explicit imports and the requested code patterns.

#### [MODIFY] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- Remove `import kotlinx.datetime.Clock as KtClock`.
- Add `import kotlinx.datetime.Clock`. (User requested `kotlin.time.Clock` but `System` belongs to `kotlinx.datetime.Clock`).
- Replace `KtClock.System.now()` with `Clock.System.now()`.

#### [MODIFY] [CompleteVisitScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/CompleteVisitScreen.kt)
- Remove aliased imports.
- Use `Clock.System.todayIn(...)`.

#### [MODIFY] [WorkOrderComponents.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderComponents.kt)
- Remove aliased imports.
- Update `KtClock.System.now()` to `Clock.System.now()`.

#### [MODIFY] [WorkOrderListScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderListScreen.kt)
- Remove aliased imports.
- Update all `KtClock.System` or `Clock.System` references.

## Verification Plan

### Automated Tests
- Run `./gradlew :shared:linkDebugFrameworkIosArm64` to verify the iOS compilation.
- Run `./gradlew :androidApp:assembleDebug` to ensure Android still works.

### Manual Verification
- None required as these are build fixes, but I will double check the resulting code doesn't break UI logic.
