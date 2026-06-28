-- Phase 5C Migration: Add profile_slug
-- Run this in your Supabase SQL editor AFTER deploying phase 5C.
-- This is safe to run on an existing database. It does not delete any data.

-- ────────────────────────────────────────────────────────────────────────────
-- 1. Add profile_slug column (nullable initially so existing rows don't fail)
-- ────────────────────────────────────────────────────────────────────────────
ALTER TABLE public.profiles
  ADD COLUMN IF NOT EXISTS profile_slug TEXT;

-- ────────────────────────────────────────────────────────────────────────────
-- 2. Populate slug for all existing rows from profile_name.
--    Handles: lowercase, spaces → hyphens, multiple consecutive spaces,
--    leading/trailing spaces, and strips non-alphanumeric chars (except hyphens).
-- ────────────────────────────────────────────────────────────────────────────
UPDATE public.profiles
SET profile_slug = lower(
    regexp_replace(
        regexp_replace(
            trim(profile_name),
            '[^a-zA-Z0-9\s-]', '', 'g'  -- strip special chars
        ),
        '\s+', '-', 'g'                 -- spaces → single hyphen
    )
)
WHERE profile_slug IS NULL OR profile_slug = '';

-- ────────────────────────────────────────────────────────────────────────────
-- 3. Make profile_slug NOT NULL now that all rows are populated
-- ────────────────────────────────────────────────────────────────────────────
ALTER TABLE public.profiles
  ALTER COLUMN profile_slug SET NOT NULL,
  ALTER COLUMN profile_slug SET DEFAULT 'personal';

-- ────────────────────────────────────────────────────────────────────────────
-- 4. Add unique index on (user_id, profile_slug).
--    Note: username stays unique at account level (one username per account).
--          profile_slug is unique per user (not globally), since the same
--          slug like "work" can exist for different users.
-- ────────────────────────────────────────────────────────────────────────────
CREATE UNIQUE INDEX IF NOT EXISTS profiles_user_id_profile_slug_idx
  ON public.profiles (user_id, profile_slug);

-- ────────────────────────────────────────────────────────────────────────────
-- 5. Verify the results
-- ────────────────────────────────────────────────────────────────────────────
-- Run this to confirm the migration looks correct before proceeding:
-- SELECT id, username, profile_name, profile_slug, user_id FROM public.profiles LIMIT 20;
