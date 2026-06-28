import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || ''
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || ''

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

export type Profile = {
  id: string
  user_id: string
  profile_name: string
  /** Immutable URL-safe slug. Source of truth for routing. e.g. "real-estate" */
  profile_slug: string
  username: string
  full_name: string | null
  job_title: string | null
  company: string | null
  phone: string | null
  email: string | null
  website: string | null
  theme_color_hex: string
  is_dark_theme: boolean
  is_public: boolean
  profile_photo_url: string | null
  company_logo_url: string | null
}
