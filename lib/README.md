# Introduction

Geidea Payment SDK for Android provides tools for easy integration of
Geidea Payment Gateway services into your Android app.

# Dependencies

- Kotlin
- Kotlin stdlib
- Kotlin coroutines
- KotlinX JSON serialization
- AndroidX

# Build

## Building the SDK

```bash
./gradlew :lib:assemble
```

# Release

1. Setup authentication needed to publish (once)

The SDK is released in the Github Packages Maven repository. To publish
you need to authenticate yourself with Github Personal Access Token. It
must be defined in `gradle.properties` as a pair of properties:

```
GithubMavenUsername=<YOUR GITHUB USERNAME>
GithubMavenPassword=<YOUR PERSONAL ACCESS TOKEN>
```
Your token must be generated with write permissions for Github Packages.

2. Increase ARTIFACT_VERSION in publish.gradle

3. Tag with the version in master branch
```bash
git tag <VERSION>
```

4. Publish in local Maven repo (for verification)
```bash
./gradlew :lib:clean
./gradlew :lib:publishAllPublicationsToMavenLocal
```

5. Verify the artifacts (manually)

Go to your local Maven repo in `~/.m2/repositories` and verify all
artifacts are in-place in `net/geidea/paymentsdk/paymentsdk/<VERSION>/`
folder. Check javadoc and kdoc for private or internal APIs accidentally made public.

6. Publish packages to the Github Maven repository

```bash
./gradlew :lib:publishAllPublicationsToGithubMavenRepository
```

7. Publish the sample app source code (manually)

Commit and push any new source code from the 'app' module to
https://github.com/GeideaSolutions/Android-SDK/

8. Optionally update the 'Integration Guide' document in case there are newly documented features
and push it under the /docs folder.

9. Tag the github sample app with the same <VERSION> as the SDK release.

10. Create a release in GitHub

- Go to https://github.com/GeideaSolutions/Android-SDK/releases
- Click "Draft a new release"
- In the "Release title" type the <VERSION>
- In the release notes list all new features and fixes 

# Documentation

## KDoc HTML archive

Generating the KDoc HTML archive with Dokka
```bash
./gradlew :lib:dokkaHtml
```

## Javadoc archive

Generating the Javadoc archive with Dokka
```bash
./gradlew :lib:dokkaJavadoc
```

## Integration guide

A Word document describing how to integrate the various APIs and
features in the SDK.

# Test

JUnit4 and MockK are used for testing. Robolectric for testing the UI
components (text input fields and forms).

Running unit tests:
```bash
./gradlew :lib:test
```