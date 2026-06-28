import { getProfile } from '@/lib/data'
import { NextRequest, NextResponse } from 'next/server'

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ username: string; profile_slug: string }> }
) {
  const resolvedParams = await params
  const profile = await getProfile(resolvedParams.username, resolvedParams.profile_slug)

  if (!profile) {
    return new NextResponse('Profile not found', { status: 404 })
  }

  const vcard = [
    'BEGIN:VCARD',
    'VERSION:3.0',
    `N:${profile.full_name || profile.username};;;`,
    `FN:${profile.full_name || profile.username}`,
    profile.job_title ? `TITLE:${profile.job_title}` : null,
    profile.company ? `ORG:${profile.company}` : null,
    profile.email ? `EMAIL;TYPE=INTERNET:${profile.email}` : null,
    profile.phone ? `TEL;TYPE=CELL:${profile.phone}` : null,
    profile.website
      ? `URL:${profile.website.startsWith('http') ? profile.website : `https://${profile.website}`}`
      : null,
    'END:VCARD',
  ]
    .filter(Boolean)
    .join('\n')

  return new NextResponse(vcard, {
    headers: {
      'Content-Type': 'text/vcard; charset=utf-8',
      'Content-Disposition': `attachment; filename="contact.vcf"`,
    },
  })
}
