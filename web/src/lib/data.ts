import { supabase, Profile } from './supabase'

/**
 * Fetch a profile by username and optional profile_slug.
 * Queries the profile_slug column directly — no fragile in-memory transforms.
 */
export async function getProfile(
  username: string,
  profileSlug: string = 'personal'
): Promise<Profile | null> {
  const { data, error } = await supabase
    .from('profiles')
    .select('*')
    .eq('username', username)
    .eq('profile_slug', profileSlug)
    .eq('is_public', true)
    .limit(1)
    .single()

  if (error || !data) {
    return null
  }

  return data
}

/**
 * Fetch the default (personal) profile for a username.
 * Falls back to the first public profile if no 'personal' slug exists.
 */
export async function getDefaultProfile(username: string): Promise<Profile | null> {
  // Try 'personal' slug first
  const personal = await getProfile(username, 'personal')
  if (personal) return personal

  // Fallback: first public profile for this username
  const { data } = await supabase
    .from('profiles')
    .select('*')
    .eq('username', username)
    .eq('is_public', true)
    .limit(1)
    .single()

  return data ?? null
}
