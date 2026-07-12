import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = {
  title: "Privacy Policy",
  description: "How TapCard collects, uses, and protects your information.",
};

export default function PrivacyPolicy() {
  return (
    <div className="min-h-screen bg-[#0b0e14] text-zinc-100">
      <div className="mx-auto max-w-3xl px-6 py-16">
        <Link href="/" className="text-sm text-teal-300 hover:text-teal-200">
          ← Back to TapCard
        </Link>
        <h1 className="mt-6 text-3xl font-extrabold tracking-tight">
          Privacy Policy
        </h1>
        <p className="mt-2 text-sm text-zinc-500">Last updated: July 12, 2026</p>

        <div className="mt-10 space-y-8 leading-7 text-zinc-300">
          <section>
            <h2 className="text-xl font-semibold text-zinc-100">What TapCard is</h2>
            <p className="mt-3">
              TapCard is a digital business card. You choose what contact
              information to put on your card, and you choose to make it public
              at a shareable URL (tapcard.space/u/your-username) so people you
              share it with can view it and save your contact details.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">
              Information you provide
            </h2>
            <p className="mt-3">
              When you create an account we collect your email address and a
              password (handled by Supabase Auth; we never see your password in
              plain text). When you build a card you may provide your name, job
              title, company, phone number, email, website, username, and
              optional profile photo and company logo. This information exists
              solely so it can be displayed on the card you share. Cards you
              mark as public are visible to anyone with the link — that is the
              product working as intended.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">
              Information collected automatically
            </h2>
            <p className="mt-3">
              When someone views a public profile page we record a basic
              analytics event: the profile viewed, how it was reached (e.g. NFC
              tap, QR scan, or direct link), the browser user agent, and a
              timestamp. This helps card owners see how their card is being
              used. We do not use third-party advertising trackers on profile
              pages.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">
              Where your data lives
            </h2>
            <p className="mt-3">
              Your account and card data are stored with Supabase (hosted on
              AWS in the United States). Uploaded images are stored in Supabase
              Storage. The app also keeps a local copy of your cards on your
              device so it works offline.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">
              What we don&apos;t do
            </h2>
            <p className="mt-3">
              We do not sell your personal information. We do not share it with
              third parties except the infrastructure providers named above,
              and we do not use your contact details for marketing.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">
              Deleting your data
            </h2>
            <p className="mt-3">
              You can make any card private at any time, which removes it from
              public view. To delete your account and all associated data,
              contact us at the email below and we will remove it.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">Contact</h2>
            <p className="mt-3">
              Questions about this policy or your data:{" "}
              <a
                href="mailto:mattjhagen0@gmail.com"
                className="text-teal-300 hover:text-teal-200"
              >
                mattjhagen0@gmail.com
              </a>
            </p>
          </section>
        </div>
      </div>
    </div>
  );
}
