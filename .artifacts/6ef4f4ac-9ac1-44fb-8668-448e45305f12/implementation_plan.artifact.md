# iOS Build Fix: Automatic Firebase Linking via CocoaPods

Since you are working on **Windows**, you cannot manually add packages in Xcode. To fix the iOS build on GitHub CI, we will move the iOS dependency management into Gradle using the **CocoaPods plugin**. This will allow the GitHub runner (which is a Mac) to automatically fetch and link Firebase for you.

## Proposed Changes

### [Shared Module]

#### [MODIFY] [shared/build.gradle.kts](file:///C:/Users/yoga/Desktop/New/FireAndSafty/shared/build.gradle.kts)

- Add the `kotlin("native.cocoapods")` plugin.
- Add a `cocoapods` configuration block.
- Define the Firebase pods (`FirebaseCore`, `FirebaseAuth`, `FirebaseFirestore`) directly in Gradle.
- Remove the manual framework generation loop as the CocoaPods plugin will now handle this.
- Move `linkerOpts("-lsqlite3")` into the CocoaPods framework configuration.

### [Build Configuration]

#### [MODIFY] [gradle/libs.versions.toml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/gradle/libs.versions.toml)
- Ensure no version conflicts with the new plugin.

## User Actions Required

1. **GitHub Push**: Once I apply these changes, you need to push them to GitHub.
2. **CI/CD Build**: The GitHub Action will now automatically run `pod install` (handled by the Kotlin plugin) and link the Firebase libraries during the build.
3. **No Xcode Needed**: You don't need to touch Xcode on Windows.

## Open Questions
> [!IMPORTANT]
> The `iosApp` project on GitHub might need a small update to use the generated `.xcworkspace` instead of `.xcodeproj` if the CI script is very specific. However, most modern KMP CI templates handle this transition automatically.

## Verification Plan

### Automated Tests
- Run Gradle sync in Android Studio. It should pass on Windows (the CocoaPods plugin will be idle but won't error).
- Monitor the GitHub CI build. The linker errors for `FIRAuth` and `FIRFirestore` should disappear.

### Manual Verification
- Verify the Android app still builds and runs correctly.
