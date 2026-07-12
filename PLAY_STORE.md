# Publishing TapCard to the Google Play Store

Everything code-side is already wired up. What remains is account setup, a
one-time signing key, and the Play Console listing — all manual steps only the
account owner can do.

## 1. One-time: Play Console account

1. Register at [play.google.com/console](https://play.google.com/console/signup)
   ($25 one-time fee).
2. Complete identity verification (Google requires it before you can publish).

## 2. One-time: create your upload keystore

Run this locally (requires a JDK). **Never commit the keystore.**

```bash
keytool -genkeypair -v \
  -keystore tapcard-upload.keystore \
  -alias tapcard -keyalg RSA -keysize 2048 -validity 10000
```

Remember the store password, alias (`tapcard`), and key password. Then add
four GitHub repository secrets (Settings → Secrets and variables → Actions):

| Secret | Value |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | `base64 -w0 tapcard-upload.keystore` output |
| `ANDROID_KEYSTORE_PASSWORD` | the store password |
| `ANDROID_KEY_ALIAS` | `tapcard` |
| `ANDROID_KEY_PASSWORD` | the key password |

Keep the keystore file backed up somewhere safe (a password manager). With
Play App Signing (the default), Google holds the final app signing key and
this keystore is only your *upload* key — if you lose it, Google can reset it,
but it's a support process you want to avoid.

Once the secrets exist, every push to `main` produces a signed
`tapcard-release-aab` artifact on the Android Build workflow run — that `.aab`
is what you upload to Play.

## 3. Per-release: version bump

Play rejects uploads that reuse a `versionCode`. Before each release, bump in
`TapCard/app/build.gradle.kts`:

```kotlin
versionCode = 2      // must increase every upload
versionName = "1.1"  // what users see
```

## 4. Play Console listing (first release)

1. **Create app** → name "TapCard", default language, App, Free.
2. **App content** section (all required):
   - Privacy policy URL: `https://tapcard.space/privacy` (already live)
   - Data safety form — declare: email address (account), name/phone/email
     (user-provided card content, shared publicly by user choice), analytics
     (profile view events). No data sold, no ads.
   - Content rating questionnaire (it's a utility app — rates Everyone)
   - Target audience: 18+ or 13+ (no child-directed content)
3. **Store listing**:
   - Short + full description
   - App icon 512×512 (upscale `TapCard/app/src/main/res/mipmap-xxxhdpi/` art
     or regenerate from `ios/TapCard/Resources/.../Icon-1024.png`)
   - Feature graphic 1024×500
   - At least 2 phone screenshots (take from a device/emulator)
4. **Release** → Testing → Internal testing first. Upload the `.aab` from the
   CI artifact, add your own email as a tester, verify install from Play.
5. Promote to Production when satisfied. First reviews take a few days.

## 5. Requirements already handled in this repo

- `targetSdk = 35` (Play requires 35+ for new apps since Aug 2025)
- Release builds are minified with ProGuard rules for Supabase/Ktor/Room/Coil
- Upload-key signing config reads from env vars only — no secrets in the repo
- CI builds a signed `.aab` when the four secrets above are present
- Privacy policy page at tapcard.space/privacy
- Adaptive launcher icon
