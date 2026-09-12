# Implementation Plan - Fix Backend Deployment Failure

The backend deployment is failing because I added a new field (`scheduledEnd`) to the `WorkOrder` entity, but I didn't update the actual database table on Render. Since Hibernate is set to "validate", it crashes when it sees the database is missing that column.

## Proposed Changes

### [backend] component

#### [NEW] [V6__add_scheduled_end_to_work_order.sql](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/resources/db/migration/V6__add_scheduled_end_to_work_order.sql)
- Create a new Flyway migration file to add the `scheduled_end` column to the `work_orders` table.

## Verification Plan

### Manual Verification
1. **Apply Change**: I will create the migration file.
2. **Push to GitHub**: You must push this change to GitHub:
    ```bash
    git add .
    git commit -m "Add missing DB migration for scheduledEnd"
    git push
    ```
3. **Wait for Render**: Wait for Render to show "Live".
4. **Login**: The app should now start and allow login correctly.
