import { redirect } from 'next/navigation'

/**
 * Compatibility redirect: /card/{username} → /u/{username}
 * Preserves any ?ref= query params on the redirect.
 */
export default async function CardRedirectPage({
  params,
  searchParams,
}: {
  params: Promise<{ username: string }>
  searchParams: Promise<Record<string, string>>
}) {
  const resolvedParams = await params
  const resolvedSearch = await searchParams
  const query = new URLSearchParams(resolvedSearch).toString()
  const destination = `/u/${resolvedParams.username}${query ? `?${query}` : ''}`
  redirect(destination)
}
