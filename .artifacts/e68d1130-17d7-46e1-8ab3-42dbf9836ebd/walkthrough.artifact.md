# Walkthrough - Sign-up JSON Error Fix

I have updated the backend to include the `role` field in the authentication response, which fixes the deserialization error you encountered.

## Changes Made

### Backend
- **[AuthenticationResponse.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/dto/AuthenticationResponse.java)**: Added the `role` field to the response data object.
- **[AuthenticationService.java](file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/AuthenticationService.java)**: Updated both registration and authentication methods to include the user's role in the response.

## Next Steps

> [!IMPORTANT]
> You MUST push these changes to GitHub so Render can update your live API. Run these commands in your terminal:

```bash
git add .
git commit -m "Fix sign-up JSON response error"
git push
```

After pushing, wait a few minutes for Render to show "Live" on your dashboard, then try signing up again in the Android app.

render_diffs(file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/web/dto/AuthenticationResponse.java)
render_diffs(file:///C:/Users/yoga/Desktop/New/FireAndSafty/backend/src/main/java/com/yoga/firesafety/backend/domain/service/AuthenticationService.java)
