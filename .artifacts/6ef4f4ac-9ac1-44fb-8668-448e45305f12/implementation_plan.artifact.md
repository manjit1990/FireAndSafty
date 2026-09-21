# Fix iOS Build Task Ambiguity and CI Workflow

The introduction of the `native.cocoapods` plugin has changed the internal Gradle task names for building the iOS framework. The previous task `:shared:linkDebugFrameworkIosArm64` has been replaced by `:shared:linkPodDebugFrameworkIosArm64`. Additionally, the CI pipeline needs to be updated to support CocoaPods.

## Proposed Changes

### [CI/CD Configuration]

#### [MODIFY] [.github/workflows/ios-build.yml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/.github/workflows/ios-build.yml)

- **Add CocoaPods Setup**: Install CocoaPods on the macOS runner.
- **Run Pod Install**: Execute `pod install` in the `iosApp` directory to generate the Xcode workspace and link dependencies.
- **Update Build Task**: Change the Gradle task name from `:shared:linkDebugFrameworkIosArm64` to `:shared:linkPodDebugFrameworkIosArm64`.
- **Switch to Workspace**: Update the `xcodebuild` command to use `-workspace iosApp.xcworkspace` instead of `-project iosApp.xcodeproj`.
- **Update Artifact Paths**: Ensure the framework upload path matches the new CocoaPods output structure if necessary.

## Verification Plan

### Manual Verification
- Once the changes are pushed, the GitHub Action will trigger.
- Monitor the "Build KMP iOS Shared Framework" step to ensure `:shared:linkPodDebugFrameworkIosArm64` passes.
- Monitor the "Build iOS App (Xcode)" step to ensure it correctly uses the workspace and linked pods.

### Automated Tests
- Since I am on Windows and cannot run the iOS linker or CocoaPods commands locally, verification depends on the CI results. I have verified that the Gradle configuration is correct for CocoaPods usage.
