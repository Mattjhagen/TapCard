import ProfileView from '@/components/ProfileView'
import { getProfile } from '@/lib/data'
import { Metadata } from 'next'

export async function generateMetadata({ params }: { params: Promise<{ username: string }> }): Promise<Metadata> {
  const resolvedParams = await params
  const profile = await getProfile(resolvedParams.username)
  if (!profile) return { title: 'Profile Not Found' }
  
  return {
    title: `${profile.full_name || resolvedParams.username} - Digital Business Card`,
    description: profile.job_title || 'View my digital business card on TapCard.',
    openGraph: {
      images: profile.profile_photo_url ? [profile.profile_photo_url] : [],
    }
  }
}

export default async function Page({ params }: { params: Promise<{ username: string }> }) {
  const resolvedParams = await params
  return <ProfileView username={resolvedParams.username} />
}
