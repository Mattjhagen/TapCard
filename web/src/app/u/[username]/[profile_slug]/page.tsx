import ProfileView from '@/components/ProfileView'
import { getProfile } from '@/lib/data'
import { Metadata } from 'next'

export async function generateMetadata({
  params,
}: {
  params: Promise<{ username: string; profile_slug: string }>
}): Promise<Metadata> {
  const resolvedParams = await params
  const profile = await getProfile(resolvedParams.username, resolvedParams.profile_slug)
  if (!profile) return { title: 'Profile Not Found' }

  return {
    title: `${profile.full_name || resolvedParams.username} (${profile.profile_name}) | TapCard`,
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
  params: Promise<{ username: string; profile_slug: string }>
  searchParams: Promise<{ ref?: string }>
}) {
  const resolvedParams = await params
  const resolvedSearch = await searchParams
  return (
    <ProfileView
      username={resolvedParams.username}
      profileSlug={resolvedParams.profile_slug}
      refSource={resolvedSearch.ref}
    />
  )
}
