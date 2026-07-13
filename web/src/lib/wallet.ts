import crypto from 'crypto'
import forge from 'node-forge'

function base64url(str: string | Buffer): string {
  const base64 = typeof str === 'string'
    ? Buffer.from(str).toString('base64')
    : str.toString('base64')
  return base64.replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_')
}

/**
 * Signs a JWT payload for Google Wallet using Google Service Account private key.
 */
export function signGoogleWalletJwt(payload: any): string {
  const privateKeyRaw = process.env.GOOGLE_PRIVATE_KEY || ''
  const clientEmail = process.env.GOOGLE_SERVICE_ACCOUNT_EMAIL || ''

  if (!privateKeyRaw || !clientEmail) {
    throw new Error('Missing Google Service Account credentials.')
  }

  // Parse private key (handle base64 and actual PEM string with literal \n)
  let privateKey = privateKeyRaw
  if (!privateKey.includes('-----BEGIN PRIVATE KEY-----')) {
    try {
      const decoded = Buffer.from(privateKey, 'base64').toString('utf8')
      if (decoded.includes('-----BEGIN PRIVATE KEY-----')) {
        privateKey = decoded
      }
    } catch (e) {
      // not base64
    }
  }

  privateKey = privateKey.replace(/\\n/g, '\n')

  const header = {
    alg: 'RS256',
    typ: 'JWT',
  }

  const iat = Math.floor(Date.now() / 1000)
  const exp = iat + 3600 // 1 hour expiration

  const jwtPayload = {
    iss: clientEmail,
    aud: 'google',
    typ: 'savetowallet',
    iat,
    exp,
    ...payload,
  }

  const tokenInput = `${base64url(JSON.stringify(header))}.${base64url(JSON.stringify(jwtPayload))}`
  const signer = crypto.createSign('RSA-SHA256')
  signer.update(tokenInput)
  const signature = signer.sign(privateKey)
  return `${tokenInput}.${base64url(signature)}`
}

/**
 * Signs manifest.json to create the Apple Wallet signature file using local OpenSSL.
 */
export function signAppleWalletManifest(manifestContent: string): Buffer {
  const passCertRaw = process.env.APPLE_PASS_CERTIFICATE || ''
  const passKeyRaw = process.env.APPLE_PASS_PRIVATE_KEY || ''
  const wwdrCertRaw = process.env.APPLE_WWDR_CERTIFICATE || ''
  const keyPassword = process.env.APPLE_PASS_PRIVATE_KEY_PASSWORD || ''

  if (!passCertRaw || !passKeyRaw || !wwdrCertRaw) {
    throw new Error('Missing Apple Pass certificates / private key.')
  }

  const parsePem = (raw: string, header: string): string => {
    if (raw.includes(header)) {
      return raw.replace(/\\n/g, '\n')
    }
    try {
      const decoded = Buffer.from(raw, 'base64').toString('utf8')
      if (decoded.includes(header)) {
        return decoded
      }
    } catch (e) {}
    return raw
  }

  const passCert = parsePem(passCertRaw, '-----BEGIN CERTIFICATE-----')
  const passKey = parsePem(passKeyRaw, '-----BEGIN PRIVATE KEY-----')
  const wwdrCert = parsePem(wwdrCertRaw, '-----BEGIN CERTIFICATE-----')

  try {
    const cert = forge.pki.certificateFromPem(passCert)
    
    let privateKey
    if (keyPassword) {
      privateKey = forge.pki.decryptRsaPrivateKey(passKey, keyPassword)
    } else {
      privateKey = forge.pki.privateKeyFromPem(passKey)
    }

    const wwdr = forge.pki.certificateFromPem(wwdrCert)

    const p7 = forge.pkcs7.createSignedData()
    p7.content = forge.util.createBuffer(manifestContent, 'utf8')

    p7.addCertificate(cert)
    p7.addCertificate(wwdr)

    p7.addSigner({
      key: privateKey,
      certificate: cert,
      digestAlgorithm: forge.pki.oids.sha256,
      authenticatedAttributes: [
        {
          type: forge.pki.oids.contentType,
          value: forge.pki.oids.data,
        },
        {
          type: forge.pki.oids.messageDigest,
        },
        {
          type: forge.pki.oids.signingTime,
          value: new Date() as any,
        },
      ],
    })

    p7.sign({ detached: true })

    const derBytes = forge.asn1.toDer(p7.toAsn1()).getBytes()
    return Buffer.from(derBytes, 'binary')
  } catch (error: any) {
    throw new Error(`Failed to sign Apple Wallet pass via node-forge: ${error.message || error}`)
  }
}
