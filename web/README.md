# TapCard Web Platform

This is the Next.js frontend for TapCard, providing public profiles for users to share their digital business cards.

## Deployment to Vercel

1. Create a new project on Vercel and import the `web/` folder from this repository.
2. In the Vercel project settings, set the root directory to `web/`.
3. Go to **Settings > Domains** and add `tapcard.space`.
4. Add the following environment variables in the **Settings > Environment Variables** section:

### Required Environment Variables

```env
NEXT_PUBLIC_SUPABASE_URL=your-supabase-project-url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your-supabase-anon-key
```

## Local Development

1. Run `npm install` to install dependencies.
2. Copy `.env.example` to `.env.local` and add your keys.
3. Run `npm run dev` to start the development server on `localhost:3000`.
