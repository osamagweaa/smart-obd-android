# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

Smart OBD Android — a Kotlin/Android app connecting to vehicle OBD-II interfaces via Bluetooth (ELM327) for real-time diagnostics. Single-module Gradle project, no backend services.

### Build & test commands

Standard Gradle commands (see `README.md` for context):

- **Build**: `./gradlew assembleDebug`
- **Unit tests**: `./gradlew testDebugUnitTest`
- **Lint**: `./gradlew lintDebug` (has pre-existing lint errors; exits non-zero)
- **All checks**: `./gradlew check` (combines tests + lint)

### Environment requirements

- **JDK 21** (or 17+) — required by AGP 8.12.3
- **Android SDK** installed at `/opt/android-sdk` with:
  - `platforms;android-36`, `build-tools;36.0.0`, `platform-tools`
  - Build-tools 35 is auto-installed by Gradle on first build
- Environment variables must be set:
  ```
  ANDROID_HOME=/opt/android-sdk
  ANDROID_SDK_ROOT=/opt/android-sdk
  PATH includes $ANDROID_HOME/cmdline-tools/latest/bin and $ANDROID_HOME/platform-tools
  ```
  These are configured in `~/.bashrc`.

### Known gotchas

- `./gradlew lintDebug` exits with code 1 due to 5 pre-existing lint errors (e.g. `MissingPermission` in `BluetoothScanScreen.kt`). This is expected and not a setup issue.
- First Gradle build downloads Gradle 8.13 distribution and all dependencies — takes ~3 minutes. Subsequent builds are fast (~5s).
- The project uses JitPack for `obd-java-api` and `kotlin-obd-api` dependencies — requires internet access.
- No Android emulator or physical device is available in the cloud VM, so `connectedAndroidTest` (instrumented tests) cannot run. Unit tests and lint are fully functional.
- This is a pure Android app with no Docker, no backend services, and no database. The only external dependency is an optional remote LLM API (`apifreellm.com`).
