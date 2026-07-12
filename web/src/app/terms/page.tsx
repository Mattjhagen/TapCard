import type { Metadata } from "next";
import Link from "next/link";

export const metadata: Metadata = {
  title: "Terms of Service",
  description: "Terms and conditions for using TapCard.",
};

export default function TermsOfService() {
  return (
    <div className="min-h-screen bg-[#0b0e14] text-zinc-100 font-sans antialiased">
      <div className="mx-auto max-w-3xl px-6 py-16">
        <Link href="/" className="text-sm text-teal-300 hover:text-teal-200">
          ← Back to TapCard
        </Link>
        <h1 className="mt-6 text-3xl font-extrabold tracking-tight">
          Terms of Service
        </h1>
        <p className="mt-2 text-sm text-zinc-500">Last updated: July 12, 2026</p>

        <div className="mt-10 space-y-8 leading-7 text-zinc-300">
          <section>
            <h2 className="text-xl font-semibold text-zinc-100">1. Acceptance of Terms</h2>
            <p className="mt-3">
              By using TapCard, you agree to these Terms of Service. If you do not agree, please do not use the application.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">2. Service Description</h2>
            <p className="mt-3">
              TapCard is a digital business card platform that allows you to share your contact details via NFC, QR codes, and custom links. TapCard is open-source under the MIT license, and you can self-host the backend or use our hosted service.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">3. User Responsibility</h2>
            <p className="mt-3">
              You are responsible for all information you post on your public digital card. You agree not to post false, misleading, offensive, or unlawful content. Using the card to spam or distribute malicious links is strictly prohibited.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">4. Content Visibility</h2>
            <p className="mt-3">
              Any card you mark as public is indexable and visible to anyone with the URL. Please do not publish sensitive information (like home addresses or personal phone numbers) unless you intend for it to be publicly viewable.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">5. Disclaimer of Warranty</h2>
            <p className="mt-3">
              The service is provided &quot;as is&quot; and &quot;as available&quot; without warranties of any kind, either express or implied, including but not limited to the implied warranties of merchantability or fitness for a particular purpose.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">6. Limitation of Liability</h2>
            <p className="mt-3">
              In no event shall TapCard or its creators be liable for any direct, indirect, incidental, special, or consequential damages arising out of the use or inability to use the service.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">7. Changes to Terms</h2>
            <p className="mt-3">
              We reserve the right to modify these terms at any time. Your continued use of the service after changes are posted constitutes acceptance of the new terms.
            </p>
          </section>

          <section>
            <h2 className="text-xl font-semibold text-zinc-100">8. Contact</h2>
            <p className="mt-3">
              If you have any questions about these terms, please contact us at:{" "}
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
