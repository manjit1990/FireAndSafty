# Implementation Plan - API Deployment to Render

This plan outlines the steps to deploy your backend API to the cloud (Render) so that multiple devices can share the same data.

## User Review Required

> [!IMPORTANT]
> To deploy to Render, you will need a **GitHub account**. You must push your project to a private repository for Render to access it.

> [!NOTE]
> Render provides a free tier for both web services and PostgreSQL, which is perfect for demo purposes.

## Proposed Steps

### Step 1: Push Project to GitHub
1. Create a new private repository on GitHub.
2. Initialize Git in your project root: `git init`.
3. Add all files: `git add .`.
4. Commit: `git commit -m "Initial commit for deployment"`.
5. Push to GitHub using the instructions provided by GitHub.

### Step 2: Deploy Database on Render
1. Log in to [Render](https://render.com).
2. Click **New** > **PostgreSQL**.
3. Name it `firesafety-db`.
4. Once created, note down the **Internal Database URL**, **Username**, and **Password**.

### Step 3: Deploy Backend on Render
1. Click **New** > **Web Service**.
2. Connect your GitHub repository.
3. Name the service (e.g., `firesafety-api`).
4. **Environment:** Choose `Docker` (it will automatically use the `Dockerfile` in the root/backend).
5. **Environment Variables:** Add the following:
    - `DATABASE_URL`: (Paste your Internal Database URL)
    - `DATABASE_USERNAME`: (From Render Postgres settings)
    - `DATABASE_PASSWORD`: (From Render Postgres settings)
    - `JWT_SECRET`: (Generate a long random string)

### Step 4: Update Android App
#### [MODIFY] [FireSafetyApi.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)
- Set `useProduction = true`.
- Update `productionUrl` with the new URL provided by Render (e.g., `https://firesafety-api.onrender.com/api/v1`).

## Verification Plan

### Manual Verification
1. Once the Render deployment is successful, build the Android APK: `./gradlew :androidApp:assembleDebug`.
2. Install the APK on **two different phones**.
3. Create a user on Phone A.
4. Log in as Admin on Phone B.
5. Verify that Phone B can see the user created on Phone A in the "User Management" section.
