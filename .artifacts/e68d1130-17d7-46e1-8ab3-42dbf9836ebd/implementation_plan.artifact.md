# Implementation Plan - Fix Sign-up JSON Error

This plan addresses the deserialization error during sign-up where the app expects a `role` field from the server that is currently missing.

## Proposed Changes

### [backend] component

#### [MODIFY] [AuthenticationResponse.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/dto/AuthenticationResponse.java)
- Add the `role` field to the response DTO.
- Import the `Role` enum.

#### [MODIFY] [AuthenticationService.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/AuthenticationService.java)
- Update `register` and `authenticate` methods to include the user's `role` in the `AuthenticationResponse` builder.

## Verification Plan

### Manual Verification
1. Apply the changes.
2. **Push to GitHub:** You must push these changes to GitHub (`git add .`, `git commit`, `git push`) so Render can redeploy your API.
3. Wait for Render to show "Live".
4. Try to sign up again from the Android app.
