import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  async redirects() {
    return [
      // Compatibility: legacy /card/{username} → /u/{username} (308 permanent)
      {
        source: '/card/:username',
        destination: '/u/:username',
        permanent: true,
      },
    ]
  },
}

export default nextConfig
