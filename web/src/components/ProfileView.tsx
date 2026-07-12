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

        <footer className="mt-auto pb-4 pt-6 z-10 w-full flex flex-col gap-3">
          {/* vCard link uses profile_slug directly — no inline transform */}
          <a href={`/api/vcard/${username}/${profile.profile_slug}`}
             className="flex items-center justify-center gap-2 w-full py-4 rounded-xl font-bold text-white transition-transform hover:scale-[1.02] active:scale-95 shadow-lg"
             style={{ backgroundColor: themeColor }}>
            <Download size={20} />
            Save Contact
          </a>

          <div className="grid grid-cols-2 gap-3">
            <a href={`/api/wallet/apple/${username}/${profile.profile_slug}`}
               className="flex items-center justify-center gap-2 py-3 px-2 rounded-xl font-semibold bg-black text-white hover:bg-zinc-800 transition-all border border-zinc-800 hover:border-zinc-700 hover:scale-[1.02] active:scale-95 text-xs shadow-md">
              <svg className="w-4 h-4 fill-current" viewBox="0 0 170 170">
                <path d="M150.37 130.25c-2.45 5.66-5.35 10.87-8.71 15.66-4.58 6.53-8.33 11.05-11.22 13.56-4.48 4.12-9.28 6.23-14.42 6.35-3.69 0-8.14-1.05-13.32-3.18-5.19-2.12-9.97-3.17-14.34-3.17-4.58 0-9.49 1.05-14.75 3.17-5.26 2.13-9.5 3.24-12.74 3.35-4.34.13-9.13-1.92-14.38-6.17-3.29-2.65-7.14-7.23-11.55-13.75-4.75-7-8.91-15.67-12.5-26-3.83-11.08-5.75-21.75-5.75-32 0-14.08 3.52-25.79 10.56-35.13 7.04-9.33 16.08-14 27.13-14 4.13 0 9.04 1.2 14.75 3.58 5.71 2.38 9.5 3.57 11.38 3.57 1.63 0 5.48-1.15 11.56-3.46 6.08-2.3 10.83-3.41 14.25-3.32 12.3.94 21.84 5.47 28.62 13.58-10.78 6.53-16.07 15.48-15.88 26.85.2 9.07 3.56 16.63 10.08 22.68 6.53 6.05 14.28 9.27 23.25 9.67-2.2 6.66-4.94 13.06-8.22 19.19zm-34.84-122c0 8.08-2.9 15.4-8.71 21.94-5.81 6.53-12.98 10.27-21.5 11.23.07-1.12.1-2.21.1-3.27 0-7.75 2.94-15.02 8.81-21.8 5.88-6.78 13.25-10.74 21.1-11.87.13 1.13.2 2.45.2 3.77z"/>
              </svg>
              Apple Wallet
            </a>
            <a href={`/api/wallet/google/${username}/${profile.profile_slug}`}
               className="flex items-center justify-center gap-2 py-3 px-2 rounded-xl font-semibold bg-black text-white hover:bg-zinc-800 transition-all border border-zinc-800 hover:border-zinc-700 hover:scale-[1.02] active:scale-95 text-xs shadow-md">
              <svg className="w-4 h-4" viewBox="0 0 48 48">
                <path fill="#4285F4" d="M37.5 12H10.5C8.01 12 6 14.01 6 16.5v15C6 33.99 8.01 36 10.5 36h27c2.49 0 4.5-2.01 4.5-4.5v-15c0-2.49-2.01-4.5-4.5-4.5z"/>
                <path fill="#34A853" d="M10.5 14.25h27c1.24 0 2.25 1.01 2.25 2.25v1.5H8.25v-1.5c0-1.24 1.01-2.25 2.25-2.25z"/>
                <path fill="#EA4335" d="M37.5 15h-27C9.12 15 8 16.12 8 17.5V19h32v-1.5c0-1.38-1.12-2.5-2.5-2.5z"/>
                <path fill="#FBBC05" d="M40 23v8.5c0 1.38-1.12 2.5-2.5 2.5h-27C9.12 34 8 32.88 8 31.5V23h32z"/>
                <circle cx="24" cy="24" r="5" fill="#FFF"/>
                <path fill="#4285F4" d="M24 20c-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4-1.79-4-4-4zm0 6c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"/>
              </svg>
              Google Wallet
            </a>
          </div>
        </footer>
      </div>
    </div>
  )
}
