import Foundation
import Supabase

/// Thin wrapper around the supabase-swift SDK - mirrors SupabaseClientProvider.kt /
/// SupabaseAuthRepositoryImpl.kt / SyncProfileRepositoryImpl.kt on Android.
///
/// NOTE: written against supabase-swift v2's documented API surface
/// (`signUp(email:password:)`, `signIn(email:password:)`, `from(_:)`, `storage.from(_:)`).
/// This could not be compiled locally (no Xcode/macOS toolchain in this environment) -
/// if the resolved package version renamed a method (e.g. `signIn` -> `signInWithPassword`),
/// Xcode's error will point at the exact line to fix.
final class SupabaseService {
    static let shared = SupabaseService()

    let client: SupabaseClient?

    private init() {
        if let url = AppConfig.supabaseURL, let key = AppConfig.supabaseAnonKey {
            client = SupabaseClient(supabaseURL: url, supabaseKey: key)
        } else {
            client = nil
        }
    }

    var isConfigured: Bool { client != nil }

    // MARK: - Auth

    func currentSession() async -> Session? {
        guard let client else { return nil }
        return try? await client.auth.session
    }

    func signUp(email: String, password: String) async throws {
        guard let client else { throw SupabaseServiceError.notConfigured }
        _ = try await client.auth.signUp(email: email, password: password)
    }

    func signIn(email: String, password: String) async throws {
        guard let client else { throw SupabaseServiceError.notConfigured }
        _ = try await client.auth.signIn(email: email, password: password)
    }

    func signOut() async throws {
        guard let client else { throw SupabaseServiceError.notConfigured }
        try await client.auth.signOut()
    }

    // MARK: - Profiles

    func upsertProfile(_ profile: Profile, userId: String) async throws {
        guard let client else { throw SupabaseServiceError.notConfigured }
        let dto = profile.toRemoteDTO(userId: userId)
        try await client.from("profiles").upsert(dto).execute()
    }

    private struct ProfileIdRow: Codable { let id: String }

    func isUsernameTaken(_ username: String) async throws -> Bool {
        guard let client else { return false }
        let results: [ProfileIdRow] = try await client.from("profiles")
            .select("id")
            .eq("username", value: username)
            .execute()
            .value
        return !results.isEmpty
    }

    // MARK: - Storage

    func uploadImage(_ data: Data, path: String) async throws -> URL {
        guard let client else { throw SupabaseServiceError.notConfigured }
        let bucket = client.storage.from("profile-images")
        _ = try await bucket.upload(path, data: data, options: FileOptions(contentType: "image/jpeg", upsert: true))
        return try bucket.getPublicURL(path: path)
    }
}

enum SupabaseServiceError: Error {
    case notConfigured
}
