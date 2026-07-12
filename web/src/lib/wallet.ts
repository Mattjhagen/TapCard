import crypto from 'crypto'
import fs from 'fs'
import path from 'path'
import os from 'os'
import { execSync } from 'child_process'

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

  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'passkit-'))
  const certPath = path.join(tempDir, 'cert.pem')
  const keyPath = path.join(tempDir, 'key.pem')
  const wwdrPath = path.join(tempDir, 'wwdr.pem')
  const manifestPath = path.join(tempDir, 'manifest.json')
  const signaturePath = path.join(tempDir, 'signature')

  try {
    fs.writeFileSync(certPath, passCert)
    fs.writeFileSync(keyPath, passKey)
    fs.writeFileSync(wwdrPath, wwdrCert)
    fs.writeFileSync(manifestPath, manifestContent)

    const passinArg = keyPassword ? `-passin pass:${keyPassword}` : ''
    const cmd = `openssl smime -sign -signer "${certPath}" -inkey "${keyPath}" -certfile "${wwdrPath}" -in "${manifestPath}" -out "${signaturePath}" -outform DER -binary ${passinArg}`

    execSync(cmd, { stdio: 'ignore' })

    const signature = fs.readFileSync(signaturePath)
    return signature
  } finally {
    try {
      fs.rmSync(tempDir, { recursive: true, force: true })
    } catch (e) {
      console.error('Failed to cleanup temp dir', e)
    }
  }
}
