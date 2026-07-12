import { getProfile } from '@/lib/data'
import { signGoogleWalletJwt } from '@/lib/wallet'
import { NextRequest, NextResponse } from 'next/server'

function getSetupHtml(missing: string[]) {
  return `
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Google Wallet Configuration Required</title>
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
          color: #ff9800;
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
          border-left: 4px solid #ff9800;
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
        <h1>Google Wallet Integration Setup Required</h1>
        <p>Google Wallet generic passes require service account credentials and Issuer configurations to sign JWTs. Please add the following missing environment variables to your deployment (.env file or hosting settings):</p>
        <div class="missing-list">
          <ul>
            ${missing.map(m => `<li>${m}</li>`).join('')}
          </ul>
        </div>
        <p>Once these variables are set, the "Add to Google Wallet" button will sign the generic pass and redirect directly to Google Pay to save the pass.</p>
        <div class="footer">TapCard Platform &copy; 2026</div>
      </div>
    </body>
    </html>
  `
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
    'GOOGLE_PRIVATE_KEY',
    'GOOGLE_SERVICE_ACCOUNT_EMAIL',
    'GOOGLE_WALLET_ISSUER_ID',
    'GOOGLE_WALLET_CLASS_ID'
  ]
  const missing = required.filter(key => !process.env[key])

  if (missing.length > 0) {
    const html = getSetupHtml(missing)
    return new NextResponse(html, {
      headers: { 'Content-Type': 'text/html; charset=utf-8' },
      status: 200
    })
  }

  const issuerId = process.env.GOOGLE_WALLET_ISSUER_ID
  const classId = process.env.GOOGLE_WALLET_CLASS_ID
  const objectId = `${issuerId}.${profile.id}`

  const payload = {
    origins: ['https://tapcard.space'],
    payload: {
      genericObjects: [
        {
          id: objectId,
          classId: `${issuerId}.${classId}`,
          genericType: 'GENERIC_TYPE_UNSPECIFIED',
          hexBackgroundColor: profile.theme_color_hex || '#000000',
          logo: {
            sourceUri: {
              uri: profile.profile_photo_url || 'https://tapcard.space/logo.png',
            },
          },
          cardTitle: {
            defaultValue: { language: 'en-US', value: profile.company || 'TapCard' },
          },
          header: {
            defaultValue: { language: 'en-US', value: profile.full_name || profile.username },
          },
          subheader: {
            defaultValue: { language: 'en-US', value: profile.job_title || 'Digital Business Card' },
          },
          barcode: {
            type: 'QR_CODE',
            value: `https://tapcard.space/u/${username}/${profile_slug}`,
          },
          textModulesData: [
            profile.phone ? {
              header: 'PHONE',
              body: profile.phone,
              id: 'phone',
            } : null,
            profile.email ? {
              header: 'EMAIL',
              body: profile.email,
              id: 'email',
            } : null,
            profile.website ? {
              header: 'WEBSITE',
              body: profile.website,
              id: 'website',
            } : null,
          ].filter(Boolean),
        },
      ],
    },
  }

  try {
    const jwt = signGoogleWalletJwt(payload)
    const saveUrl = `https://pay.google.com/gp/v/save/${jwt}`
    return NextResponse.redirect(saveUrl)
  } catch (error: any) {
    return new NextResponse(`Error generating Google Wallet pass: ${error.message}`, { status: 500 })
  }
}
