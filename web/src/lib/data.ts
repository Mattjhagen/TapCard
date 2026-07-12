import { supabase, Profile } from './supabase'

const UUID_REGEX = /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/

function isUUID(val: string): boolean {
  return UUID_REGEX.test(val)
}

/**
 * Fetch a profile by username (or UUID) and optional profile_slug.
 * Queries the profile_slug column directly — no fragile in-memory transforms.
 */
export async function getProfile(
  username: string,
  profileSlug: string = 'personal'
): Promise<Profile | null> {
  let query = supabase.from('profiles').select('*')

  if (isUUID(username)) {
    query = query.eq('id', username)
  } else {
    query = query.eq('username', username)
  }

  const { data, error } = await query
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
 * Fetch the default (personal) profile for a username or UUID.
 * Falls back to the first public profile if no 'personal' slug exists.
 */
export async function getDefaultProfile(username: string): Promise<Profile | null> {
  // Try 'personal' slug first
  const personal = await getProfile(username, 'personal')
  if (personal) return personal

  let query = supabase.from('profiles').select('*')

  if (isUUID(username)) {
    query = query.eq('id', username)
  } else {
    query = query.eq('username', username)
  }

  const { data } = await query
    .eq('is_public', true)
    .limit(1)
    .single()

  return data ?? null
}
