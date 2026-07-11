import Foundation

@MainActor
final class AuthViewModel: ObservableObject {
    @Published private(set) var authState: AuthState
    @Published var errorMessage: String?

    private let service = SupabaseService.shared

    init() {
        authState = service.isConfigured ? .signedOut : .unconfigured
        Task { await refreshSession() }
    }

    func refreshSession() async {
        guard service.isConfigured else {
            authState = .unconfigured
            return
        }
        if await service.currentSession() != nil {
            authState = .signedIn
        }
    }

    func signUp(email: String, password: String) async {
        guard service.isConfigured else { authState = .unconfigured; return }
        authState = .loading
        errorMessage = nil
        do {
            try await service.signUp(email: email, password: password)
            authState = .signedIn
            AnalyticsService.log(.accountCreated)
        } catch {
            authState = .error
            errorMessage = error.localizedDescription
        }
    }

    func signIn(email: String, password: String) async {
        guard service.isConfigured else { authState = .unconfigured; return }
        authState = .loading
        errorMessage = nil
        do {
            try await service.signIn(email: email, password: password)
            authState = .signedIn
        } catch {
            authState = .error
            errorMessage = error.localizedDescription
        }
    }

    func signOut() async {
        guard service.isConfigured else { return }
        do {
            try await service.signOut()
            authState = .signedOut
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
