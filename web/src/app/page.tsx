import Link from "next/link";
import { Nfc, QrCode, Contact, Globe, Smartphone, Share2 } from "lucide-react";

const features = [
  {
    icon: Nfc,
    title: "Tap to Share",
    description:
      "Program an NFC tag from the app, then anyone can tap their phone to open your card instantly. No app required on their end.",
  },
  {
    icon: QrCode,
    title: "QR Code Fallback",
    description:
      "Every profile gets a scannable QR code you can share, print, or save to your photos for older phones without NFC.",
  },
  {
    icon: Contact,
    title: "Save to Contacts",
    description:
      "Visitors download your details as a vCard with one tap and it opens straight into their contacts app.",
  },
  {
    icon: Globe,
    title: "Public Web Profile",
    description:
      "Your card lives at tapcard.space/u/username. A clean web view of your name, title, company, and links.",
  },
  {
    icon: Smartphone,
    title: "Multiple Identities",
    description:
      "Keep separate Personal and Work cards under one account and switch between them anytime.",
  },
  {
    icon: Share2,
    title: "Always in Sync",
    description:
      "Update your info in the app and every tag, QR code, and link you have ever shared shows the latest version.",
  },
];

function ContactlessMark({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 64 64" fill="none" className={className} aria-hidden>
      <circle cx="18" cy="32" r="5" fill="currentColor" />
      <path
        d="M28 20a17 17 0 0 1 0 24M36 14a26 26 0 0 1 0 36M44 8a35 35 0 0 1 0 48"
        stroke="currentColor"
        strokeWidth="5"
        strokeLinecap="round"
      />
    </svg>
  );
}

export default function Home() {
  return (
    <div className="min-h-screen bg-[#0b0e14] text-zinc-100">
      {/* Hero */}
      <div className="relative overflow-hidden">
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            background:
              "radial-gradient(60% 80% at 15% 0%, rgba(24,210,165,0.18) 0%, transparent 60%), radial-gradient(70% 90% at 90% 100%, rgba(108,63,211,0.28) 0%, transparent 65%)",
          }}
        />
        <header className="relative mx-auto flex max-w-5xl items-center justify-between px-6 py-6">
          <div className="flex items-center gap-2 font-bold tracking-tight">
            <ContactlessMark className="h-6 w-6 text-teal-300" />
            <span className="text-lg">TapCard</span>
          </div>
          <a
            href="https://github.com/Mattjhagen/TapCard"
            target="_blank"
            rel="noopener noreferrer"
            className="rounded-full border border-white/15 px-4 py-1.5 text-sm text-zinc-300 transition-colors hover:border-white/40 hover:text-white"
          >
            GitHub
          </a>
        </header>

        <section className="relative mx-auto flex max-w-5xl flex-col items-center px-6 pb-24 pt-16 text-center">
          <ContactlessMark className="mb-8 h-20 w-20 text-teal-300" />
          <h1 className="max-w-2xl text-4xl font-extrabold leading-tight tracking-tight sm:text-6xl">
            Your business card,
            <br />
            <span className="bg-gradient-to-r from-teal-300 to-violet-400 bg-clip-text text-transparent">
              one tap away.
            </span>
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-8 text-zinc-400">
            TapCard turns your contact info into a digital card you share by
            tapping phones, scanning a QR code, or sending a link. Recipients
            save it to their contacts in seconds.
          </p>
          <div className="mt-10 flex flex-col gap-4 sm:flex-row">
            <a
              href="https://github.com/Mattjhagen/TapCard/actions/workflows/android-build.yml"
              target="_blank"
              rel="noopener noreferrer"
              className="rounded-full bg-teal-300 px-7 py-3 font-semibold text-[#0b0e14] transition-colors hover:bg-teal-200"
            >
              Get the Android App
            </a>
            <Link
              href="/u/mattjhagen"
              className="rounded-full border border-white/15 px-7 py-3 font-semibold text-zinc-200 transition-colors hover:border-white/40 hover:text-white"
            >
              See a Live Card
            </Link>
          </div>
        </section>
      </div>

      {/* Features */}
      <section className="mx-auto max-w-5xl px-6 pb-24">
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {features.map(({ icon: Icon, title, description }) => (
            <div
              key={title}
              className="rounded-2xl border border-white/10 bg-white/[0.03] p-6"
            >
              <Icon className="h-7 w-7 text-teal-300" />
              <h3 className="mt-4 font-semibold">{title}</h3>
              <p className="mt-2 text-sm leading-6 text-zinc-400">
                {description}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* How it works */}
      <section className="mx-auto max-w-5xl px-6 pb-24">
        <h2 className="text-center text-2xl font-bold tracking-tight sm:text-3xl">
          How it works
        </h2>
        <ol className="mx-auto mt-10 grid max-w-3xl gap-8 sm:grid-cols-3">
          {[
            ["Create your card", "Add your name, title, company, and links in the app."],
            ["Claim your URL", "Pick a username and get tapcard.space/u/you."],
            ["Tap or scan", "Program an NFC tag or show your QR code. Done."],
          ].map(([title, description], index) => (
            <li key={title} className="text-center">
              <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-teal-300 to-violet-400 font-bold text-[#0b0e14]">
                {index + 1}
              </div>
              <h3 className="mt-4 font-semibold">{title}</h3>
              <p className="mt-2 text-sm leading-6 text-zinc-400">{description}</p>
            </li>
          ))}
        </ol>
      </section>

      <footer className="border-t border-white/10">
        <div className="mx-auto flex max-w-5xl flex-col items-center justify-between gap-4 px-6 py-8 text-sm text-zinc-500 sm:flex-row">
          <div className="flex items-center gap-2">
            <ContactlessMark className="h-4 w-4 text-teal-300" />
            <span>TapCard</span>
          </div>
          <div className="flex gap-6">
            <a
              href="https://github.com/Mattjhagen/TapCard"
              target="_blank"
              rel="noopener noreferrer"
              className="transition-colors hover:text-zinc-300"
            >
              GitHub
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
