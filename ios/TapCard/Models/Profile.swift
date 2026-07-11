import Foundation

struct Profile: Identifiable, Codable, Equatable {
    var id: String
    var userId: String
    var profileName: String
    /// Immutable URL-safe slug derived from profileName on creation, e.g. "Real Estate" -> "real-estate"
    var profileSlug: String
    var fullName: String
    var jobTitle: String
    var company: String
    var phone: String
    var email: String
    var website: String
    var username: String
    var themeColorHex: String
    var isDarkTheme: Bool
    var isPublic: Bool
    var isPendingSync: Bool
    var profilePhotoLocalPath: String?
    var companyLogoLocalPath: String?
    var profilePhotoUrl: String?
    var companyLogoUrl: String?

    init(
        id: String = UUID().uuidString,
        userId: String = "",
        profileName: String = "Personal",
        profileSlug: String = "personal",
        fullName: String = "",
        jobTitle: String = "",
        company: String = "",
        phone: String = "",
        email: String = "",
        website: String = "",
        username: String = "",
        themeColorHex: String = "#000000",
        isDarkTheme: Bool = true,
        isPublic: Bool = true,
        isPendingSync: Bool = false,
        profilePhotoLocalPath: String? = nil,
        companyLogoLocalPath: String? = nil,
        profilePhotoUrl: String? = nil,
        companyLogoUrl: String? = nil
    ) {
        self.id = id
        self.userId = userId
        self.profileName = profileName
        self.profileSlug = profileSlug
        self.fullName = fullName
        self.jobTitle = jobTitle
        self.company = company
        self.phone = phone
        self.email = email
        self.website = website
        self.username = username
        self.themeColorHex = themeColorHex
        self.isDarkTheme = isDarkTheme
        self.isPublic = isPublic
        self.isPendingSync = isPendingSync
        self.profilePhotoLocalPath = profilePhotoLocalPath
        self.companyLogoLocalPath = companyLogoLocalPath
        self.profilePhotoUrl = profilePhotoUrl
        self.companyLogoUrl = companyLogoUrl
    }

    /// Single source of truth for slug generation - mirrors ProfileViewModel.computeSlug on Android.
    static func computeSlug(from name: String) -> String {
        var slug = name.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        slug = slug.replacingOccurrences(of: "[^a-z0-9\\s-]", with: "", options: .regularExpression)
        slug = slug.replacingOccurrences(of: "\\s+", with: "-", options: .regularExpression)
        slug = slug.replacingOccurrences(of: "-{2,}", with: "-", options: .regularExpression)
        return slug
    }

    var shareableURL: String {
        let usernameOrId = username.isEmpty ? id : username
        if profileSlug == "personal" || profileSlug == "default" {
            return "\(AppConfig.baseURL)/u/\(usernameOrId)"
        } else {
            return "\(AppConfig.baseURL)/u/\(usernameOrId)/\(profileSlug)"
        }
    }
}

/// Wire format for Supabase `profiles` table - mirrors RemoteProfileDto.kt / supabase_schema.sql.
struct RemoteProfileDTO: Codable {
    let id: String
    let userId: String
    let profileName: String
    let profileSlug: String
    let fullName: String?
    let jobTitle: String?
    let company: String?
    let phone: String?
    let email: String?
    let username: String
    let website: String?
    let themeColorHex: String?
    let isDarkTheme: Bool
    let isPublic: Bool
    let profilePhotoUrl: String?
    let companyLogoUrl: String?

    enum CodingKeys: String, CodingKey {
        case id
        case userId = "user_id"
        case profileName = "profile_name"
        case profileSlug = "profile_slug"
        case fullName = "full_name"
        case jobTitle = "job_title"
        case company
        case phone
        case email
        case username
        case website
        case themeColorHex = "theme_color_hex"
        case isDarkTheme = "is_dark_theme"
        case isPublic = "is_public"
        case profilePhotoUrl = "profile_photo_url"
        case companyLogoUrl = "company_logo_url"
    }
}

extension Profile {
    func toRemoteDTO(userId overrideUserId: String? = nil) -> RemoteProfileDTO {
        RemoteProfileDTO(
            id: id,
            userId: overrideUserId ?? userId,
            profileName: profileName,
            profileSlug: profileSlug,
            fullName: fullName.isEmpty ? nil : fullName,
            jobTitle: jobTitle.isEmpty ? nil : jobTitle,
            company: company.isEmpty ? nil : company,
            phone: phone.isEmpty ? nil : phone,
            email: email.isEmpty ? nil : email,
            username: username,
            website: website.isEmpty ? nil : website,
            themeColorHex: themeColorHex,
            isDarkTheme: isDarkTheme,
            isPublic: isPublic,
            profilePhotoUrl: profilePhotoUrl,
            companyLogoUrl: companyLogoUrl
        )
    }
}
