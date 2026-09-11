# Implementation Plan - Compose Multiplatform for Web (Wasm)

This plan outlines the steps to add Web support to your project using **Compose Multiplatform (Wasm)**. This will allow your app to run in any modern web browser using the same UI code from your `:shared` module.

## User Review Required

> [!IMPORTANT]
> - **Browser Compatibility**: Wasm (WebAssembly) requires modern browsers (Chrome 119+, Firefox 120+, Safari 17.4+).
> - **Navigation**: Web navigation usually requires a slightly different approach for URL handling (Deeplinking), but for this demo, we will implement the same screen-flow as the mobile app.

## Proposed Changes

### 1. Build Configuration

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/settings.gradle.kts)
- Include the new `:webApp` module: `include(":webApp")`.

#### [MODIFY] [shared/build.gradle.kts](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/shared/build.gradle.kts)
- Add `wasmJs` target to the Kotlin configuration.
- Configure browser webpack for the shared module.

---

### 2. New Web Application Module

#### [NEW] [webApp/build.gradle.kts](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/webApp/build.gradle.kts)
- Create the build script for the web module.
- Apply Kotlin Multiplatform and Compose plugins.
- Add dependencies for `:shared` and Koin.

#### [NEW] [webApp/src/wasmJsMain/kotlin/Main.kt](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/webApp/src/wasmJsMain/kotlin/Main.kt)
- Implement the web entry point (`ComposeViewport`).
- Initialize Koin for the web platform.
- Call the shared `MainApp` or root composable.

#### [NEW] [webApp/src/wasmJsMain/resources/index.html](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/webApp/src/wasmJsMain/resources/index.html)
- Create the HTML entry point.
- Add a `<canvas>` element where Compose will render the UI.
- Include the generated script tags.

---

### 3. Shared Logic Updates

#### [MODIFY] [DriverFactory.kt](file:///C:/Users/yoga/AndroidStudioProjects/FireSafetyServiceManagement/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/data/local/DriverFactory.kt)
- Ensure the `SqlDriver` can be created for the Web platform (using SqlDelight's web driver).

## Verification Plan

### Manual Verification
1. Run the command: `./gradlew :webApp:wasmJsBrowserRun`.
2. Open the browser at `http://localhost:8080`.
3. Verify that the **Login Screen** appears exactly as it does on Android.
4. Test the **Admin Dashboard** and **Schedule** screens in the browser.
5. Check if the layout is responsive when resizing the browser window.
