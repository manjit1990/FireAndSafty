# Implementation Plan - Ultimate Premium Dark UI

This plan aims to deliver the "best" UI experience by refining the Dark Theme to be ultra-premium and ensuring every single component in the app correctly responds to theme changes.

## Proposed Changes

### [shared] component (Theming)

#### [MODIFY] [DesignSystem.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/theme/DesignSystem.kt)
- **Deep Dark Palette**: Refine the dark theme to use an AMOLED-friendly background (`#000000` or very deep navy `#0A0C14`).
- **Glassmorphism**: Use surface variants with slight transparency for cards to create a modern layered look.
- **Accents**: Use vibrant primary and secondary colors that "pop" against the dark background.
- **Typography**: Refine font weights and sizes for a more professional feel.

#### [MODIFY] [FireSafetyTheme.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/theme/FireSafetyTheme.kt)
- Ensure all `MaterialTheme` parameters (colorScheme, typography, shapes) are properly configured.

---

### [shared] component (Screens Overhaul)

I will systematically go through every screen and replace hardcoded colors (`Color.White`, `Color.Black`, `Color.Gray`, `#F5F7FA`, etc.) with theme-aware tokens.

#### [MODIFY] All Screens:
- [LoginScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/LoginScreen.kt)
- [SignupScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/auth/SignupScreen.kt)
- [AdminDashboardScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/AdminDashboardScreen.kt)
- [WorkOrderListScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderListScreen.kt)
- [UserManagementScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/UserManagementScreen.kt)
- [ScheduleWorkOrderScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/admin/ScheduleWorkOrderScreen.kt)
- [WorkOrderDetailsScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderDetailsScreen.kt)
- [InspectionFormScreen.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/inspection/InspectionFormScreen.kt)
- [WorkOrderComponents.kt](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/src/commonMain/kotlin/com/yoga/firesafety/shared/presentation/dashboard/WorkOrderComponents.kt)

### Enhancements:
- **Gradients**: More sophisticated gradients for login/signup backgrounds.
- **Shadows & Elevation**: Use `tonalElevation` and refined shapes (more rounded corners, e.g., 24dp).
- **Icons**: Use high-quality icons with consistent tinting.

## Verification Plan

### Manual Verification
1. **Toggle Theme**: Switch the phone to Dark Mode and verify every screen looks "premium".
2. **Component Check**: Ensure text is readable (high contrast) and icons are clearly visible.
3. **Consistency**: Verify that all screens use the same palette and spacing.
