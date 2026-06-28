/**
 * Environment validator for the web platform.
 *
 * Call validateEnvironment() before any Supabase operations.
 * If env vars are missing, render <UnconfiguredPage /> instead of crashing.
 */

export type EnvironmentStatus = {
  isConfigured: boolean
  missing: string[]
}

export function validateEnvironment(): EnvironmentStatus {
  const required: Record<string, string | undefined> = {
    NEXT_PUBLIC_SUPABASE_URL: process.env.NEXT_PUBLIC_SUPABASE_URL,
    NEXT_PUBLIC_SUPABASE_ANON_KEY: process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY,
  }

  const missing = Object.entries(required)
    .filter(([, value]) => !value || value.trim() === '')
    .map(([key]) => key)

  return {
    isConfigured: missing.length === 0,
    missing,
  }
}
