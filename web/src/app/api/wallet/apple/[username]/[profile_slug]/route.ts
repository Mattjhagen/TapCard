import { getProfile } from '@/lib/data'
import { signAppleWalletManifest } from '@/lib/wallet'
import { NextRequest, NextResponse } from 'next/server'
import crypto from 'crypto'
import AdmZip from 'adm-zip'

function getSetupHtml(missing: string[]) {
  return `
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Apple Wallet Configuration Required</title>
      <style>
        body {
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
          background-color: #121212;
          color: #e0e0e0;
          display: flex;
          align-items: center;
          justify-content: center;
          min-height: 100vh;
          margin: 0;
          padding: 20px;
        }
        .card {
          background-color: #1e1e1e;
          border-radius: 12px;
          padding: 30px;
          max-width: 500px;
          width: 100%;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
          border: 1px solid #333;
        }
        h1 {
          color: #007aff;
          font-size: 24px;
          margin-top: 0;
          margin-bottom: 15px;
        }
        p {
          line-height: 1.6;
          color: #b0b0b0;
        }
        .missing-list {
          background-color: #2d2d2d;
          border-left: 4px solid #007aff;
          padding: 10px 15px;
          border-radius: 4px;
          margin: 20px 0;
        }
        ul {
          margin: 0;
          padding-left: 20px;
        }
        li {
          margin: 8px 0;
          font-family: monospace;
          color: #fff;
        }
        .footer {
          margin-top: 25px;
          font-size: 12px;
          color: #666;
          text-align: center;
        }
      </style>
    </head>
    <body>
      <div class="card">
        <h1>Apple Wallet Integration Setup Required</h1>
        <p>Apple Wallet passes (.pkpass) require cryptographic signing certificates and keys to be loaded on the server. Please add the following missing environment variables to your deployment (.env file or hosting settings):</p>
        <div class="missing-list">
          <ul>
            ${missing.map(m => `<li>${m}</li>`).join('')}
          </ul>
        </div>
        <p>Once these variables are set, the "Add to Apple Wallet" button will generate a cryptographically valid .pkpass file which iOS devices will automatically import into the native Apple Wallet app.</p>
        <div class="footer">TapCard Platform &copy; 2026</div>
      </div>
    </body>
    </html>
  `
}

const tinyPngBase64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII='
const tinyPngBuffer = Buffer.from(tinyPngBase64, 'base64')

function hexToRgb(hex: string): string {
  const cleanHex = hex.replace('#', '')
  if (cleanHex.length !== 6) return 'rgb(24, 24, 27)'
  const r = parseInt(cleanHex.substring(0, 2), 16)
  const g = parseInt(cleanHex.substring(2, 4), 16)
  const b = parseInt(cleanHex.substring(4, 6), 16)
  return `rgb(${r}, ${g}, ${b})`
}

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ username: string; profile_slug: string }> }
) {
  const resolvedParams = await params
  const { username, profile_slug } = resolvedParams

  const profile = await getProfile(username, profile_slug)
  if (!profile) {
    return new NextResponse('Profile not found', { status: 404 })
  }

  // Check missing environment variables
  const required = [
    'APPLE_PASS_CERTIFICATE',
    'APPLE_PASS_PRIVATE_KEY',
    'APPLE_WWDR_CERTIFICATE'
  ]
  const missing = required.filter(key => !process.env[key])

  if (missing.length > 0) {
    const html = getSetupHtml(missing)
    return new NextResponse(html, {
      headers: { 'Content-Type': 'text/html; charset=utf-8' },
      status: 200
    })
  }

  try {
    const teamId = process.env.APPLE_TEAM_IDENTIFIER || '8W8X2P8A8P'
    const passTypeId = process.env.APPLE_PASS_TYPE_IDENTIFIER || 'pass.space.tapcard'

    const passJson = {
      formatVersion: 1,
      passTypeIdentifier: passTypeId,
      serialNumber: profile.id,
      teamIdentifier: teamId,
      organizationName: 'TapCard',
      description: 'TapCard Digital Business Card',
      logoText: profile.company || 'TapCard',
      foregroundColor: 'rgb(255, 255, 255)',
      backgroundColor: hexToRgb(profile.theme_color_hex),
      generic: {
        primaryFields: [
          {
            key: 'name',
            value: profile.full_name || profile.username
          }
        ],
        secondaryFields: [
          profile.job_title ? {
            key: 'title',
            value: profile.job_title
          } : null
        ].filter(Boolean),
        auxiliaryFields: [
          profile.company ? {
            key: 'company',
            label: 'Company',
            value: profile.company
          } : null,
          profile.phone ? {
            key: 'phone',
            label: 'Phone',
            value: profile.phone
          } : null,
          profile.email ? {
            key: 'email',
            label: 'Email',
            value: profile.email
          } : null
        ].filter(Boolean),
        backFields: [
          profile.website ? {
            key: 'website',
            label: 'Website',
            value: profile.website.startsWith('http') ? profile.website : `https://${profile.website}`
          } : null
        ].filter(Boolean)
      },
      barcodes: [
        {
          format: 'PKBarcodeFormatQR',
          message: `https://tapcard.space/u/${username}/${profile_slug}`,
          messageEncoding: 'iso-8859-1'
        }
      ]
    }

    const zip = new AdmZip()
    const files: { [key: string]: Buffer } = {}

    // Add pass.json
    files['pass.json'] = Buffer.from(JSON.stringify(passJson, null, 2))

    // Add default images
    files['icon.png'] = tinyPngBuffer
    files['icon@2x.png'] = tinyPngBuffer
    files['logo.png'] = tinyPngBuffer
    files['logo@2x.png'] = tinyPngBuffer

    // If profile has a photo, fetch it and use it as thumbnail
    if (profile.profile_photo_url) {
      try {
        const photoRes = await fetch(profile.profile_photo_url)
        if (photoRes.ok) {
          const arrayBuffer = await photoRes.arrayBuffer()
          const photoBuffer = Buffer.from(arrayBuffer)
          files['thumbnail.png'] = photoBuffer
          files['thumbnail@2x.png'] = photoBuffer
        }
      } catch (e) {
        console.error('Failed to fetch profile photo for thumbnail', e)
      }
    }

    // Build manifest.json
    const manifest: { [key: string]: string } = {}
    for (const [filename, buffer] of Object.entries(files)) {
      const hash = crypto.createHash('sha1').update(buffer).digest('hex')
      manifest[filename] = hash
    }
    const manifestContent = JSON.stringify(manifest, null, 2)
    files['manifest.json'] = Buffer.from(manifestContent)

    // Sign manifest to create signature file
    const signatureBuffer = signAppleWalletManifest(manifestContent)
    files['signature'] = signatureBuffer

    // Pack into ZIP
    for (const [filename, buffer] of Object.entries(files)) {
      zip.addFile(filename, buffer)
    }

    const pkpassBuffer = zip.toBuffer()

    return new NextResponse(new Uint8Array(pkpassBuffer), {
      headers: {
        'Content-Type': 'application/vnd.apple.pkpass',
        'Content-Disposition': `attachment; filename="${username}_card.pkpass"`
      }
    })
  } catch (error: any) {
    return new NextResponse(`Error generating Apple Wallet pass: ${error.message}`, { status: 500 })
  }
}
