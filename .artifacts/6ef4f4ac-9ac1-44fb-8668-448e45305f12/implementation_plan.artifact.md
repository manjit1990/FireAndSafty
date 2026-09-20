# Setup iOS CI/CD Build Pipeline

The user wants to generate an iOS build through a CI/CD pipeline (GitHub Actions). The current workflow only builds the shared framework. I will update it to build and archive the actual iOS application.

## User Review Required

> [!WARNING]
> **Code Signing**: Building a production-ready `.ipa` for installation on a physical iPhone requires Apple Developer certificates and provisioning profiles. These cannot be easily automated in CI without adding secrets (p12 files, provisioning profiles) to the repository settings.
>
> I will configure the pipeline to perform a **"Build and Archive"** which verifies the code is correct. To get an installable file, you will eventually need to configure "GitHub Secrets" with your Apple credentials.

## Proposed Changes

### [CI/CD]

#### [MODIFY] [ios-build.yml](file:///C:/Users/yoga/Desktop/New/FireAndSafty/.github/workflows/ios-build.yml)

- **Update Build Steps**:
    - Ensure the KMP shared framework is built first.
    - Add a step to build the Xcode project using `xcodebuild`.
    - Configure it to use a "Generic iOS Device" destination.
    - Add a step to upload the resulting build artifact (`.app` or `.xcarchive`) so it can be downloaded from the GitHub Actions tab.

## Verification Plan

### Manual Verification
- Commit the updated workflow file.
- Push to GitHub.
- Go to the **Actions** tab on your GitHub repository.
- Manually trigger the "iOS Build Pipeline" (if `workflow_dispatch` is enabled) or wait for the push trigger.
- Check the logs to ensure both the Gradle (KMP) and Xcode build steps pass.
- Verify that a build artifact appears at the end of the run.
