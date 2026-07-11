import Foundation

enum VCardService {
    /// Builds a vCard 3.0 payload for a profile, matching the fields the web platform's
    /// /api/vcard/[username] route generates server-side.
    static func vCardString(for profile: Profile) -> String {
        var lines = ["BEGIN:VCARD", "VERSION:3.0"]
        lines.append("FN:\(profile.fullName.isEmpty ? profile.username : profile.fullName)")
        if !profile.jobTitle.isEmpty { lines.append("TITLE:\(profile.jobTitle)") }
        if !profile.company.isEmpty {
            lines.append("ORG:\(profile.company)")
        }
        if !profile.phone.isEmpty { lines.append("TEL;TYPE=CELL:\(profile.phone)") }
        if !profile.email.isEmpty { lines.append("EMAIL:\(profile.email)") }
        if !profile.website.isEmpty { lines.append("URL:\(profile.website)") }
        lines.append("URL:\(profile.shareableURL)")
        lines.append("END:VCARD")
        return lines.joined(separator: "\r\n")
    }

    /// Writes the vCard to a temp file so it can be shared (AirDrop, Files, Messages) or
    /// imported directly into Contacts via a share-sheet/QuickLook action.
    static func writeTempFile(for profile: Profile) -> URL? {
        let vcard = vCardString(for: profile)
        let filename = "\(profile.username.isEmpty ? "contact" : profile.username).vcf"
        let url = FileManager.default.temporaryDirectory.appendingPathComponent(filename)
        do {
            try vcard.write(to: url, atomically: true, encoding: .utf8)
            return url
        } catch {
            return nil
        }
    }
}
