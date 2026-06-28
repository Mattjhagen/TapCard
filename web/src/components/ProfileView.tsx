import { getDefaultProfile, getProfile } from '@/lib/data'
import { notFound } from 'next/navigation'
import Image from 'next/image'
import { Briefcase, Mail, Globe, Phone, Download } from 'lucide-react'
import { validateEnvironment } from '@/lib/environment'
import { logEvent, parseRef } from '@/lib/analytics'
import UnconfiguredPage from './UnconfiguredPage'

type Props = {
  username: string
  /** profile_slug from URL — defaults to 'personal' for the /u/[username] root route. */
  profileSlug?: string
  /** Raw ?ref= query param value. Will be normalized before storage. */
  refSource?: string
}

export default async function ProfileView({ username, profileSlug = 'personal', refSource }: Props) {
  // Guard: show dev-friendly page if env vars are missing
  const env = validateEnvironment()
  if (!env.isConfigured) {
    return <UnconfiguredPage missing={env.missing} />
  }

  const profile = profileSlug === 'personal'
    ? await getDefaultProfile(username)
    : await getProfile(username, profileSlug)

  if (!profile) {
    notFound()
  }

  // Log analytics event server-side with normalized source
  const normalizedSource = parseRef(refSource)
  await logEvent(profile.id, 'profile_view', normalizedSource)

  const isDark = profile.is_dark_theme
  const themeColor = profile.theme_color_hex

  return (
    <div className={`min-h-screen font-sans antialiased transition-colors duration-300 ${isDark ? 'bg-zinc-900 text-zinc-100' : 'bg-zinc-50 text-zinc-900'}`}>
      <div className="max-w-md mx-auto min-h-screen flex flex-col p-6 shadow-2xl relative overflow-hidden" style={{ backgroundColor: isDark ? '#18181b' : '#ffffff' }}>

        {/* Dynamic Theme Gradient Background */}
        <div className="absolute top-0 left-0 right-0 h-48 opacity-20" style={{ background: `linear-gradient(to bottom, ${themeColor}, transparent)` }} />

        <main className="flex-1 flex flex-col items-center pt-8 z-10">

          <div className="relative mb-6">
            <div className="w-32 h-32 rounded-full overflow-hidden border-4" style={{ borderColor: themeColor }}>
              {profile.profile_photo_url ? (
                <Image src={profile.profile_photo_url} alt="Profile Photo" width={128} height={128} className="object-cover w-full h-full" />
              ) : (
                <div className="w-full h-full bg-zinc-300 dark:bg-zinc-700 flex items-center justify-center text-4xl text-zinc-500">
                  {profile.full_name?.charAt(0) || username.charAt(0).toUpperCase()}
                </div>
              )}
            </div>
            {profile.company_logo_url && (
              <div className="absolute -bottom-2 -right-2 w-12 h-12 rounded-full border-2 overflow-hidden bg-white shadow-lg flex items-center justify-center p-1" style={{ borderColor: isDark ? '#18181b' : '#ffffff' }}>
                 <Image src={profile.company_logo_url} alt="Company Logo" width={48} height={48} className="object-contain" />
              </div>
            )}
          </div>

          <h1 className="text-3xl font-bold mb-1 tracking-tight">{profile.full_name || username}</h1>

          {profile.job_title && (
            <p className="text-lg font-medium flex items-center gap-2 mb-1" style={{ color: themeColor }}>
              <Briefcase size={18} />
              {profile.job_title}
            </p>
          )}

          {profile.company && (
             <p className={`text-md mb-6 ${isDark ? 'text-zinc-400' : 'text-zinc-500'}`}>
               @ {profile.company}
             </p>
          )}

          {/* Identity Tag — shows display name, not slug */}
          <div className="px-3 py-1 rounded-full text-xs font-semibold uppercase tracking-wider mb-8 bg-zinc-200 dark:bg-zinc-800" style={{ color: themeColor }}>
            {profile.profile_name}
          </div>

          <div className="w-full space-y-4 mb-8">
            {profile.email && (
              <a href={`mailto:${profile.email}`} className={`flex items-center gap-4 p-4 rounded-xl transition-all hover:scale-[1.02] active:scale-95 ${isDark ? 'bg-zinc-800 hover:bg-zinc-700' : 'bg-white shadow-sm border border-zinc-100 hover:border-zinc-200'}`}>
                <div className="p-3 rounded-lg" style={{ backgroundColor: `${themeColor}20`, color: themeColor }}>
                  <Mail size={24} />
                </div>
                <div className="flex flex-col">
                  <span className="text-sm text-zinc-500">Email</span>
                  <span className="font-medium">{profile.email}</span>
                </div>
              </a>
            )}

            {profile.phone && (
              <a href={`tel:${profile.phone}`} className={`flex items-center gap-4 p-4 rounded-xl transition-all hover:scale-[1.02] active:scale-95 ${isDark ? 'bg-zinc-800 hover:bg-zinc-700' : 'bg-white shadow-sm border border-zinc-100 hover:border-zinc-200'}`}>
                <div className="p-3 rounded-lg" style={{ backgroundColor: `${themeColor}20`, color: themeColor }}>
                  <Phone size={24} />
                </div>
                <div className="flex flex-col">
                  <span className="text-sm text-zinc-500">Phone</span>
                  <span className="font-medium">{profile.phone}</span>
                </div>
              </a>
            )}

            {profile.website && (
              <a href={profile.website.startsWith('http') ? profile.website : `https://${profile.website}`} target="_blank" rel="noopener noreferrer" className={`flex items-center gap-4 p-4 rounded-xl transition-all hover:scale-[1.02] active:scale-95 ${isDark ? 'bg-zinc-800 hover:bg-zinc-700' : 'bg-white shadow-sm border border-zinc-100 hover:border-zinc-200'}`}>
                <div className="p-3 rounded-lg" style={{ backgroundColor: `${themeColor}20`, color: themeColor }}>
                  <Globe size={24} />
                </div>
                <div className="flex flex-col">
                  <span className="text-sm text-zinc-500">Website</span>
                  <span className="font-medium">{profile.website.replace(/^https?:\/\//, '')}</span>
                </div>
              </a>
            )}
          </div>

        </main>

        <footer className="mt-auto pb-4 pt-6 z-10 w-full flex justify-center">
          {/* vCard link uses profile_slug directly — no inline transform */}
          <a href={`/api/vcard/${username}/${profile.profile_slug}`}
             className="flex items-center justify-center gap-2 w-full py-4 rounded-xl font-bold text-white transition-transform hover:scale-[1.02] active:scale-95 shadow-lg"
             style={{ backgroundColor: themeColor }}>
            <Download size={20} />
            Save Contact
          </a>
        </footer>
      </div>
    </div>
  )
}
