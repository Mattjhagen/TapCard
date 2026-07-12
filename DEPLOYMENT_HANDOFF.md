# TapCard Deployment Handoff

> **Status**: Code is production-ready. All builds pass. The steps below are manual actions required before the platform is live.

---

## Step 1 — Supabase SQL (Run in This Order)

Open your Supabase project → **SQL Editor** → run these files **in order**. Each block is idempotent where possible, but order matters because `phase5c.sql` depends on `profiles` existing.

### 1A — Base Schema, Storage, Feedback, and Analytics

Run the full contents of:

```
TapCard/backend/supabase_schema.sql
```

This creates:
- `public.profiles` table with RLS policies
- `public.feedback` table with RLS policies
- `storage.buckets` entry for `profile-images`
- Storage object RLS policies
- `public.analytics_events` table with RLS policies

### 1B — Phase 5C Migration (run AFTER 1A)

Run the full contents of:

```
TapCard/backend/migrations/phase5c.sql
```

This safely adds `profile_slug` to all existing rows:
- `ALTER TABLE profiles ADD COLUMN IF NOT EXISTS profile_slug TEXT`
- Backfills slug from `profile_name` using `lower(regexp_replace(...))`
- Sets `NOT NULL` after backfill
- Creates `UNIQUE INDEX profiles_user_id_profile_slug_idx ON profiles(user_id, profile_slug)`

> **IMPORTANT**: The web platform will return 404 for all profile pages until this migration runs. `profile_slug` is the routing key.

### Verification Query

After running both files, confirm the schema is correct:

```sql
-- Should show id, username, profile_name, profile_slug for all rows
SELECT id, username, profile_name, profile_slug, is_public
FROM public.profiles
LIMIT 20;

-- Should show the unique index exists
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'profiles';
```

---

## Step 2 — Vercel Deployment

### 2A — Create Project

1. Go to [vercel.com/new](https://vercel.com/new)
2. Import the `Mattjhagen/BusinessCardNFC` GitHub repository
3. **Set Root Directory to `web/`** — this is critical

### 2B — Environment Variables

In **Settings → Environment Variables**, add exactly these two keys:

| Variable | Value |
|---|---|
| `NEXT_PUBLIC_SUPABASE_URL` | Your Supabase project URL (e.g. `https://xyzabc.supabase.co`) |
| `NEXT_PUBLIC_SUPABASE_ANON_KEY` | Your Supabase `anon` public key |

> **NEVER** add `SUPABASE_SERVICE_ROLE` to Vercel or any Android config. It is server-only.

Get these values from: Supabase Dashboard → **Settings → API**.

### 2C — Domain

1. In Vercel → **Settings → Domains**, add `tapcard.space`
2. Add the DNS records Vercel provides to your domain registrar
3. Vercel will auto-provision TLS

### 2D — Verify Deployment

After deploy completes, open:
- `https://tapcard.space` — landing page
- `https://tapcard.space/u/{your-username}` — profile (requires Supabase SQL to be run first)

---

## Step 3 — Android Release

### 3A — Firebase Configuration

The repository ships `google-services.template.json` for reference. You must provide your own real `google-services.json`:

1. Go to [Firebase Console](https://console.firebase.google.com) → Your Project → Project Settings → **Your Apps**
2. Download `google-services.json`
3. Place it at:
   ```
   TapCard/app/google-services.json
   ```
4. Verify it is listed in `.gitignore` (it is — **do not commit it**)

### 3B — Build the Release APK

```bash
cd TapCard
./gradlew assembleRelease --no-daemon
```

Output path:
```
TapCard/app/build/outputs/apk/release/app-release-unsigned.apk
```

> **WARNING**: The release APK is **unsigned** until you configure a signing keystore in `app/build.gradle.kts`. An unsigned APK cannot be installed on user devices or uploaded to Google Play.

### 3C — Signing Setup (Required Before Distribution)

1. Generate a keystore if you don't have one:
   ```bash
   keytool -genkey -v -keystore tapcard.keystore \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias tapcard
   ```
2. Add signing config to `TapCard/app/build.gradle.kts`:
   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("../../tapcard.keystore")
               storePassword = System.getenv("KEYSTORE_PASSWORD")
               keyAlias = "tapcard"
               keyPassword = System.getenv("KEY_PASSWORD")
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
           }
       }
   }
   ```
3. **Never commit the `.keystore` file**. Add to `.gitignore`.

### 3D — Physical Device QA Before Release

Run through this checklist on a real Android device with NFC:

#### Authentication
- [ ] Sign up with a new email — account created, redirected to dashboard
- [ ] Sign out — session cleared
- [ ] Sign in again — existing profiles appear

#### Profile Management
- [ ] Create a "Personal" profile — saved locally, syncs to Supabase
- [ ] Add profile photo — local preview shows, uploads on sync
- [ ] Add company logo — same as above
- [ ] Create a "Work" profile — separate identity, stored as slug `work`
- [ ] Switch between identities — correct profile shown
- [ ] Edit a field — change persists after app restart

#### Username Validation
- [ ] Enter taken username — shows "Username taken"
- [ ] Enter invalid format — shows "Invalid format" (must be `[a-z0-9-]{3,30}`)
- [ ] Enter available username — shows "Username available"
- [ ] Sign out — shows "Sign in to validate"

#### QR Code
- [ ] Generate QR — code appears for active profile
- [ ] Scan QR with another phone — opens `https://tapcard.space/u/{username}` in browser
- [ ] Export QR as PNG — saved to gallery

#### NFC
- [ ] Tap "Program NFC Tag" — app enters programming mode
- [ ] Hold phone to NFC sticker — success toast, tag written
- [ ] Tap tag with another phone — opens profile URL in browser (no app required)
- [ ] Verify URL on tag is `https://tapcard.space/u/{username}` (not `/card/`)
- [ ] Work profile NFC — URL is `https://tapcard.space/u/{username}/work`

#### Web Profile (Verify Against Live Vercel URL)
- [ ] `https://tapcard.space/u/{username}` loads Personal profile
- [ ] `https://tapcard.space/u/{username}/work` loads Work profile
- [ ] `https://tapcard.space/card/{username}` redirects to `/u/{username}` (308)
- [ ] "Save Contact" button downloads `.vcf` file
- [ ] vCard opens in Contacts app with correct name, phone, email

#### Analytics
- [ ] View profile page, then check Supabase:
  ```sql
  SELECT * FROM analytics_events ORDER BY created_at DESC LIMIT 5;
  ```
  Expect: `event_type = 'profile_view'`, `source = 'unknown'`
- [ ] Visit `https://tapcard.space/u/{username}?ref=nfc` — row shows `source = 'nfc'`
- [ ] Visit `https://tapcard.space/u/{username}?ref=qr` — row shows `source = 'qr'`

---

## Step 4 — End-to-End Smoke Test (Full Happy Path)

Run through this sequence top to bottom on a fresh device:

```
1.  Install APK on device
2.  Sign up with new account
3.  Create Personal profile:
      Full name, job title, company, email, phone, website
      Upload profile photo
      Set username (e.g. "johndoe")
      Save → verify synced to Supabase
4.  Create Work profile:
      Different job title, same username account
      slug = "work"
      Save → verify synced
5.  Generate QR (Personal) → scan → opens tapcard.space/u/johndoe       ✓
6.  Generate QR (Work)     → scan → opens tapcard.space/u/johndoe/work  ✓
7.  Program NFC tag (Personal) → tap with other phone → web profile    ✓
8.  On web — Personal profile:
      Photo loads                                                        ✓
      Name, title, company displayed                                     ✓
      Email / Phone / Website buttons work                               ✓
      "Save Contact" → .vcf downloads, imports to Contacts              ✓
      Analytics row inserted in Supabase                                 ✓
9.  On web — Work profile:
      Different profile card shown                                        ✓
      Same username, different identity                                   ✓
10. Tap tapcard.space/card/johndoe → redirects to /u/johndoe             ✓
```

---

## Step 5 — Known Blockers

| Blocker | Severity | Action Required |
|---|---|---|
| Supabase SQL not run | **Blocking** | Run `supabase_schema.sql` then `phase5c.sql` in SQL Editor |
| `profile_slug` column missing | **Blocking** | Resolved by running `phase5c.sql` |
| Vercel env vars not set | **Blocking** | Set `NEXT_PUBLIC_SUPABASE_URL` and `NEXT_PUBLIC_SUPABASE_ANON_KEY` in Vercel |
| Domain DNS not pointed | **Blocking** | Point `tapcard.space` DNS to Vercel nameservers |
| Firebase `google-services.json` missing | Partial | Crashlytics/Analytics disabled; app still runs without it |
| Release APK unsigned | Pre-release | Must configure signing before Play Store upload |

---

## Architecture Reference

```
tapcard.space/           → GitHub Pages static landing (docs/)
tapcard.space/u/*        → Vercel (web/) — Next.js dynamic profiles
tapcard.space/api/vcard  → Vercel (web/) — vCard generation
tapcard.space/card/*     → Vercel (web/) — 308 redirect to /u/*

Android app            → TapCard/
Supabase project       → profiles, feedback, analytics_events tables
Storage bucket         → profile-images (public)
```

## File Reference

| File | Purpose |
|---|---|
| `TapCard/backend/supabase_schema.sql` | Full initial schema — run first |
| `TapCard/backend/migrations/phase5c.sql` | Adds `profile_slug` — run second |
| `web/.env.example` | Template for required Vercel env vars |
| `TapCard/.env.example` | Template for the Android Gradle build's Supabase config (copy to `TapCard/.env`) |
| `TapCard/google-services.template.json` | Firebase config template |
| `docs/index.html` | Static landing page served by GitHub Pages |
