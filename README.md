# Smart OBD Android

A source-only Android application for vehicle diagnostics over Bluetooth OBD-II adapters.

## Scope

This snapshot contains the application source and build configuration currently present in the local working tree. It intentionally excludes local signing files, device configuration, build output, IDE state, bug reports, and repository history.

## Build notes

- Android/Kotlin project using Jetpack Compose.
- Release signing is intentionally not included. Provide a local `keystore.properties` and signing key outside version control when creating a release build.
- The project may require Android SDK and Gradle tooling to build; those tools are not part of this source snapshot.

## Safety

Do not commit API keys, passwords, signing keys, local SDK paths, device reports, or generated build files.
