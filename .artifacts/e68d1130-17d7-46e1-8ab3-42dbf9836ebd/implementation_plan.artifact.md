# Implementation Plan - Enhanced Scheduling & Admin Info

This plan outlines the changes to improve the "Schedule Task" UI, add Start/End time support, and display Admin information at the top.

## User Review Required

> [!NOTE]
> The backend `WorkOrder` entity already has a `completedAt` field, but we will use/add a `scheduledEnd` field to match the requirement for a planned end time.

## Proposed Changes

### [backend] component

#### [MODIFY] [WorkOrder.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/entity/WorkOrder.java)
- Ensure `scheduledEnd` field exists or add it if necessary (the Kotlin side has it).

#### [MODIFY] [WorkOrderService.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/WorkOrderService.java)
- Update `assignWorkOrder` to accept `LocalDateTime scheduledEnd`.
- Update logic to save both start and end times.

#### [MODIFY] [WorkOrderController.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/controller/WorkOrderController.java)
- Update the assignment endpoint to accept `scheduledEnd` as an optional request parameter.

---

### [shared] component

#### [MODIFY] [FireSafetyApi.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)
- Update `assignWorkOrder` to take `scheduledEnd: String?`.

#### [MODIFY] [WorkOrderRepository.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/domain/repository/WorkOrderRepository.kt) & [WorkOrderRepositoryImpl.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/repository/WorkOrderRepositoryImpl.kt)
- Update interface and implementation to support `scheduledEnd`.

#### [MODIFY] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- **Header**: Add a professional section at the top showing "Assigned by: [Admin Name]".
- **UI Redesign**:
    - Use more distinct Cards for task details.
    - Implement two time selection fields (Start Time & End Time) on a single row or stacked nicely.
    - Improve spacing and typography.
- **State**: Add `selectedEndTime` state.

## Verification Plan

### Manual Verification
1. **Push Backend**: Deploy the updated backend to Render.
2. **Login as Admin**: Verify that your name/email appears at the top of the Scheduling screen.
3. **Set Times**: Pick a date, then pick 9:00 AM as Start and 11:00 AM as End.
4. **Confirm**: Verify that the task shows the correct time range in the dashboard.
