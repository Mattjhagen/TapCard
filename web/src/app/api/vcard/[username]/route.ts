import { getProfile } from '@/lib/data'
import { NextRequest, NextResponse } from 'next/server'

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ username: string }> }
) {
  const resolvedParams = await params
  const profile = await getProfile(resolvedParams.username)

  if (!profile) {
    return new NextResponse('Profile not found', { status: 404 })
  }

  const vcard = `BEGIN:VCARD
VERSION:3.0
N:${profile.full_name || profile.username};;;
FN:${profile.full_name || profile.username}
${profile.job_title ? `TITLE:${profile.job_title}` : ''}
${profile.company ? `ORG:${profile.company}` : ''}
${profile.email ? `EMAIL;TYPE=INTERNET:${profile.email}` : ''}
${profile.phone ? `TEL;TYPE=CELL:${profile.phone}` : ''}
${profile.website ? `URL:${profile.website.startsWith('http') ? profile.website : `https://${profile.website}`}` : ''}
END:VCARD`.split('\n').filter(line => line.trim() !== '').join('\n')

  return new NextResponse(vcard, {
    headers: {
      'Content-Type': 'text/vcard; charset=utf-8',
      'Content-Disposition': `attachment; filename="contact.vcf"`
    }
  })
}
