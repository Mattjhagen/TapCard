# TapCard Platform

TapCard is a digital identity platform for Android and iOS, featuring NFC capabilities, a Supabase backend, and a web profile platform.

## Setup Instructions

### Firebase Setup (Required for Crashlytics & Analytics)
1. Go to the Firebase Console and create a new project (or use an existing one).
2. Add an Android app with the package name `com.tapcard.app`.
3. Download the `google-services.json` file.
4. Place the `google-services.json` file in the `TapCard/app/` directory. (DO NOT commit this file, it is already in `.gitignore`).
5. For reference, see `TapCard/app/google-services.template.json`.

### Supabase Setup
1. Create a Supabase project.
2. Run the SQL schema from `TapCard/backend/supabase_schema.sql`, then `TapCard/backend/migrations/phase5c.sql`.
3. Copy `TapCard/.env.example` to `TapCard/.env` and fill in your keys (the Android Gradle build reads `.env` relative to the Gradle root, i.e. `TapCard/`, not the repo root).

### iOS Setup

See `ios/README.md` for the native SwiftUI app - setup, feature parity notes vs. Android, and
important platform differences around NFC (iOS can read/write physical NDEF tags but, unlike
what a "tap phones together" name implies, no iOS app can emulate itself as a tag).

