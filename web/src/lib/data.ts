import { supabase, Profile } from './supabase'

export async function getProfile(username: string, profileName: string = 'personal'): Promise<Profile | null> {
  const { data, error } = await supabase
    .from('profiles')
    .select('*')
    .eq('username', username)
    .eq('is_public', true)
    
  if (error || !data || data.length === 0) {
    return null
  }

  // Find the exact match or default to personal if not found
  const exactMatch = data.find(p => p.profile_name.toLowerCase().replace(" ", "-") === profileName.toLowerCase())
  if (exactMatch) {
    return exactMatch
  }

  // Fallback to the first available if they only have one
  if (data.length === 1 && profileName === 'personal') {
    return data[0]
  }

  return null
}
