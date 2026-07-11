import Foundation

enum AppConfig {
    /// Single source of truth for the public profile domain.
    static let baseURL = "https://tapcard.space"

    static let supabaseURL: URL? = {
        guard let value = Bundle.main.infoDictionary?["SUPABASE_PROJECT_URL"] as? String,
              !value.isEmpty,
              let url = URL(string: value) else { return nil }
        return url
    }()

    static let supabaseAnonKey: String? = {
        guard let value = Bundle.main.infoDictionary?["SUPABASE_ANON_KEY"] as? String,
              !value.isEmpty else { return nil }
        return value
    }()

    static var isSupabaseConfigured: Bool {
        supabaseURL != nil && supabaseAnonKey != nil
    }
}
