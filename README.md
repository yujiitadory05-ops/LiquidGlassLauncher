# Liquid Glass Launcher (Android)

A starter Android home-screen launcher built with Kotlin and Jetpack Compose.

## Features
- Android HOME/DEFAULT launcher activity
- Installed app discovery using the launcher intent query
- App drawer with search filtering
- Launch installed apps
- Liquid-glass-inspired translucent panels, highlights and borders
- Clock/date display
- Ocean and violet color themes
- Settings overlay

## Build
This project uses:
- JDK 17
- Gradle 8.9
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- Compile SDK 35

Run:
```bash
gradle assembleDebug
```

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

## Build on a phone with GitHub Actions
1. Create a GitHub repository.
2. Upload the complete contents of this project.
3. Open the repository's Actions tab.
4. Run **Build Liquid Glass Launcher APK** (or push a commit).
5. Open the completed workflow run.
6. Download the `liquid-glass-launcher-debug` artifact and extract the APK.

## Set as default launcher
After installing, press Home. Android should offer a home app chooser.
Choose **Liquid Glass** and select **Always**. You can also change the Home app in Android Settings.

## Notes
- This is a functional starter launcher, not a full replacement for every OEM launcher feature.
- Some Android versions and manufacturers may restrict app visibility or launcher behavior.
- Glass appearance is simulated with translucent Compose surfaces, gradients and highlights. True backdrop refraction needs a more advanced graphics implementation.
- The project does not request internet, contacts, location, SMS, or other personal-data permissions.
