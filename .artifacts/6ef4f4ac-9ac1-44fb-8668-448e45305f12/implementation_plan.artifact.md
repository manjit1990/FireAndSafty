# Fix "Unresolved reference 'System'" in iOS Build

The iOS CI/CD build is failing because it cannot resolve the `System` property of `kotlinx.datetime.Clock` in several `commonMain` files. This is likely due to a name clash or a compiler issue in the experimental Kotlin 2.4.10 environment.

## Proposed Changes

### [Shared Presentation]

I will replace the direct usage of `Clock.System` with an aliased import or more explicit reference to avoid the "Unresolved reference 'System'" error during iOS compilation.

#### [MODIFY] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- Replace `import kotlinx.datetime.*` with explicit imports including `import kotlinx.datetime.Clock`.
- Use `Clock.System` explicitly or through an alias if necessary.

#### [MODIFY] [CompleteVisitScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/CompleteVisitScreen.kt)
- Update `kotlinx.datetime.Clock.System` to a more robust reference.

#### [MODIFY] [WorkOrderComponents.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderComponents.kt)
- Fix the `Clock.System.now()` and `kotlinx.datetime.Clock.System.now()` calls.

#### [MODIFY] [WorkOrderListScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderListScreen.kt)
- Similar fixes for `Clock.System`.

## Verification Plan

### Automated Tests
- I will attempt to trigger the CI/CD build again (or ask the user to) once the changes are applied.
- Locally, I will run the Android build to ensure no regressions.

### Manual Verification
- Verify that the app still correctly displays and handles dates/times on Android.
