import { supabase } from './supabase'

/**
 * Allowed source values for analytics.
 * Never trust raw client-supplied values — always normalize.
 */
export type AnalyticsSource = 'web' | 'qr' | 'nfc' | 'direct' | 'unknown'

/**
 * Tracked event types for profile interactions.
 */
export type AnalyticsEvent =
  | 'profile_view'
  | 'contact_download'
  | 'website_click'
  | 'email_click'
  | 'phone_click'
  | 'qr_open'
  | 'nfc_open'
  | 'direct_open'

/**
 * Normalize a raw ?ref= query param to a known AnalyticsSource.
 * Rejects unknown values — returns 'unknown' for anything unexpected.
 */
export function parseRef(ref: string | null | undefined): AnalyticsSource {
  const allowed: AnalyticsSource[] = ['web', 'qr', 'nfc', 'direct']
  const normalized = (ref ?? '').toLowerCase().trim() as AnalyticsSource
  return allowed.includes(normalized) ? normalized : 'unknown'
}

/**
 * Log an analytics event to Supabase.
 * Silently catches errors — analytics must never break the user-facing page.
 */
export async function logEvent(
  profileId: string,
  eventType: AnalyticsEvent,
  source: AnalyticsSource = 'unknown'
): Promise<void> {
  try {
    await supabase.from('analytics_events').insert({
      profile_id: profileId,
      event_type: eventType,
      source,
    })
  } catch {
    // Intentionally silent — analytics failure must not surface to users
  }
}
