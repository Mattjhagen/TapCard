# TapCard iOS

Native SwiftUI companion to the Android app in `../TapCard`, sharing the same Supabase backend
and public web profiles (`tapcard.space/u/{username}`).

## Feature parity with Android

| Feature | Android | iOS |
|---|---|---|
| Email auth (Supabase) | ✅ | ✅ |
| Local-first profile storage + cloud sync | ✅ (Room) | ✅ (JSON file store) |
| Multiple profile identities | ✅ | ✅ |
| QR code generate / share / save to gallery | ✅ | ✅ |
| Program a physical NFC tag with your profile URL | ✅ | ✅ (Core NFC) |
| Read a physical NFC tag | — | ✅ (Core NFC) |
| Share contact card via AirDrop / share sheet (.vcf) | — | ✅ |
| Google Wallet / Apple Wallet | 🚧 disabled (`WalletConfig`) | 🚧 disabled, same as Android |

### NFC: important platform difference

Android's NFC "tap to share" feature (`NfcService.kt`) does not use Host Card Emulation — it puts
the phone in NFC *reader* mode and writes a URL to a **physical NDEF tag/sticker**. Any phone
(Android or iOS) can then tap that physical tag to open the profile.

iOS has no public API for a third-party app to emulate itself as an NFC tag, so a literal
"hold two iPhones together" flow isn't possible on this platform. `NFCTagService.swift` gives
full parity with what Android actually does today: it can **write** a physical tag (`startProgramming`)
and **read** one back (`startReading`). For iPhone-to-iPhone sharing, use the QR code or the
"Share Contact Card (AirDrop)" button instead.

## Setup

This was written without access to Xcode/macOS (built in a Linux sandbox), so it has **not been
compiled**. To build it:

1. Install [XcodeGen](https://github.com/yonaskolb/XcodeGen): `brew install xcodegen`
2. From `ios/`, run:
   ```bash
   xcodegen generate
   ```
   This produces `TapCard.xcodeproj` from `project.yml` (gitignored — regenerate any time).
3. Copy the secrets template and fill in your Supabase project's values:
   ```bash
   cp TapCard/Config/Secrets.xcconfig.example TapCard/Config/Secrets.xcconfig
   ```
   Use the same `SUPABASE_PROJECT_URL` and anon/publishable key as `../TapCard/.env.example` /
   `../web/.env.example` — never the service-role key.
4. Open `TapCard.xcodeproj` in Xcode, select a development team under Signing & Capabilities
   (required for the NFC entitlement to work on a physical device), and run on a physical iPhone.
   **NFC does not work in the Simulator.**
5. Since this couldn't be compiled ahead of time, expect to fix minor Supabase Swift SDK API
   drift in `Services/SupabaseService.swift` if the resolved package version renamed a method
   (e.g. `signIn` → `signInWithPassword`) — Xcode's error will point at the exact line.

## Structure

```
ios/
  project.yml                  XcodeGen spec (generates the .xcodeproj)
  TapCard/
    Info.plist
    TapCard.entitlements        NFC reader-session entitlement
    Config/
      AppConfig.swift           Reads Supabase URL/key from Info.plist + shareable base URL
      Secrets.xcconfig.example  Template - copy to Secrets.xcconfig (gitignored)
    Models/                     Profile, RemoteProfileDTO, enums - mirrors domain/model on Android
    Services/
      SupabaseService.swift     Auth + Postgrest + Storage
      ProfileStore.swift        Local JSON persistence (mirrors Room)
      NFCTagService.swift       Read/write physical NDEF tags (Core NFC)
      QRCodeService.swift       QR generation + save to Photos
      VCardService.swift        vCard export for AirDrop / share sheet
      ImageCompressor.swift
      AnalyticsService.swift    Event names mirror AnalyticsManager.kt; no Firebase wired up yet
    ViewModels/                 AuthViewModel, ProfileViewModel
    Views/                      AuthView, OnboardingView, DashboardView, EditorView, SettingsView
```

## Known gaps vs. Android

- No offline image caching for remote profile photos/logos (Android uses Coil; here `Image(uiImage:)`
  only renders locally-picked images - wire up `AsyncImage(url:)` for remote URLs if needed).
- No Firebase Analytics/Crashlytics wiring (Android's `google-services.json` has no iOS
  equivalent configured yet - `AnalyticsService.swift` currently just print-logs in DEBUG).
- Feedback screen and "Developer Settings" (Android's hidden 7-tap menu) were not ported - not
  core to the tap-to-share flow this app is built around.
