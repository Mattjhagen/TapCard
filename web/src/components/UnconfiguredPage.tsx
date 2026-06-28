/**
 * Shown when NEXT_PUBLIC_SUPABASE_URL or NEXT_PUBLIC_SUPABASE_ANON_KEY are missing.
 * Rendered server-side — safe to use in RSC without 'use client'.
 */
export default function UnconfiguredPage({ missing }: { missing: string[] }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-zinc-900 text-zinc-100 p-6">
      <div className="max-w-lg w-full bg-zinc-800 border border-zinc-700 rounded-2xl p-8">
        <div className="text-4xl mb-4">⚙️</div>
        <h1 className="text-2xl font-bold mb-2">Supabase Not Configured</h1>
        <p className="text-zinc-400 mb-6">
          The following environment variables are missing. Add them to{' '}
          <code className="bg-zinc-700 px-1 rounded text-sm">.env.local</code>{' '}
          (local) or your Vercel project settings (production).
        </p>
        <ul className="space-y-2 mb-6">
          {missing.map((key) => (
            <li key={key} className="flex items-center gap-2 text-sm font-mono bg-zinc-700 rounded-lg px-4 py-3">
              <span className="text-red-400">✗</span>
              {key}
            </li>
          ))}
        </ul>
        <div className="text-xs text-zinc-500">
          See{' '}
          <a
            href="https://github.com/Mattjhagen/BusinessCardNFC/blob/main/web/.env.example"
            target="_blank"
            rel="noopener"
            className="underline text-zinc-400 hover:text-zinc-200"
          >
            web/.env.example
          </a>{' '}
          for required values.
        </div>
      </div>
    </div>
  )
}
