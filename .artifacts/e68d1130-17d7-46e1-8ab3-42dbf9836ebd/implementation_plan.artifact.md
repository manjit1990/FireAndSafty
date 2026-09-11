# Implementation Plan - Task Assignment & Scheduling

This plan outlines the changes to allow Admins to assign work orders to technicians and set a specific date and time for the task.

## Proposed Changes

### [backend] component

#### [MODIFY] [WorkOrderService.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/WorkOrderService.java)
- Update `assignWorkOrder` to accept a `LocalDateTime scheduledAt` parameter.
- Add logic to set the technician and the scheduled time on the `WorkOrder`.
- Update the status to `ASSIGNED`.

#### [MODIFY] [WorkOrderController.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/controller/WorkOrderController.java)
- Update the `@PatchMapping("/{id}/assign")` endpoint to accept an optional `scheduledAt` ISO date-time string.

---

### [shared] component

#### [MODIFY] [FireSafetyApi.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)
- Update `assignWorkOrder` method to take `technicianId` and `scheduledAt` (String).

#### [NEW] [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- Create a new screen where Admins can:
    - **Select Technician**: A dropdown list showing all users with the `TECHNICIAN` role.
    - **Select Date**: Using Material 3 `DatePicker`.
    - **Select Time**: Using Material 3 `TimePicker`.
- Include a "Confirm Assignment" button that calls the API.

#### [MODIFY] [MainApp.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/MainApp.kt)
- Add a new route `schedule_work_order/{orderId}` to the navigation graph.

#### [MODIFY] [AdminDashboardScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/AdminDashboardScreen.kt)
- Update the `onClick` handler of work order items to navigate to the scheduling screen.

## Verification Plan

### Manual Verification
1. **Push Backend Changes**: Deploy the updated backend to Render.
2. **Login as Admin**: Open the app and log in.
3. **Select Unscheduled Task**: Click on a work order that says "Unscheduled".
4. **Assign & Schedule**:
    - Pick a technician from the list.
    - Pick a date and time.
    - Click "Confirm Assignment".
5. **Verify Dashboard**: The work order should now show the assigned technician (in a future update) and the specific scheduled time instead of "Unscheduled".
