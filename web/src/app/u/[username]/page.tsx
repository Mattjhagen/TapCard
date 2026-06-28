import ProfileView from '@/components/ProfileView'
import { getDefaultProfile } from '@/lib/data'
import { Metadata } from 'next'

export async function generateMetadata({
  params,
}: {
  params: Promise<{ username: string }>
}): Promise<Metadata> {
  const resolvedParams = await params
  const profile = await getDefaultProfile(resolvedParams.username)
  if (!profile) return { title: 'Profile Not Found' }

  return {
    title: `${profile.full_name || resolvedParams.username} | TapCard`,
    description: profile.job_title || 'View my digital business card on TapCard.',
    openGraph: {
      images: profile.profile_photo_url ? [profile.profile_photo_url] : [],
    },
  }
}

export default async function Page({
  params,
  searchParams,
}: {
  params: Promise<{ username: string }>
  searchParams: Promise<{ ref?: string }>
}) {
  const resolvedParams = await params
  const resolvedSearch = await searchParams
  return (
    <ProfileView
      username={resolvedParams.username}
      profileSlug="personal"
      refSource={resolvedSearch.ref}
    />
  )
}
