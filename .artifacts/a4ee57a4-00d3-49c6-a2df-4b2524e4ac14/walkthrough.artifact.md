# Walkthrough - Backend Cloud Deployment Guide

I have prepared your backend for cloud hosting on **Render.com**. Follow these steps to make your APIs live.

## 1. Project Files Ready
- **[Dockerfile](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/backend/Dockerfile)**: Added a configuration file that tells Render how to build and run your Spring Boot app.
- **[FireSafetyApi.kt](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)**: Updated the code to allow easy switching between Local (Emulator) and Production (Cloud) URLs.

## 2. Deployment Steps

### Step A: Push to GitHub
1. Create a **Private Repository** on GitHub.
2. Push your entire project folder to this repository.

### Step B: Create Database on Render
1. Log in to [Render.com](https://render.com).
2. Click **New +** > **PostgreSQL**.
3. Name it `firesafety-db`.
4. Once created, copy the **Internal Database URL**.

### Step C: Create Web Service on Render
1. Click **New +** > **Web Service**.
2. Connect your GitHub repository.
3. Select **Docker** as the Runtime (Render will automatically find your `backend/Dockerfile`).
4. Add these **Environment Variables** in the "Environment" tab:
    - `DATABASE_URL`: (The URL you copied in Step B)
    - `DATABASE_USERNAME`: (From your Render Postgres dashboard)
    - `DATABASE_PASSWORD`: (From your Render Postgres dashboard)
    - `JWT_SECRET`: `AnyLongRandomString123!`
5. Click **Deploy Web Service**.

## 3. Connect Mobile App
1. Once Render shows "Live", copy your app URL (e.g., `https://firesafety-v1.onrender.com`).
2. Open **[FireSafetyApi.kt](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/remote/FireSafetyApi.kt)**.
3. Paste your URL into `productionUrl`.
4. Change `useProduction = true`.
5. Run the app!

> [!TIP]
> Using Render's Free Tier means the first request might take ~30 seconds to "wake up" the server if it hasn't been used recently.
