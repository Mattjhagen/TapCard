import Foundation

enum SyncStatus {
    case savedLocally
    case uploading
    case syncing
    case synced
    case syncFailed
    case signInToSync

    var label: String {
        switch self {
        case .savedLocally: return "Offline: Saved Locally"
        case .uploading: return "Uploading image..."
        case .syncing: return "Syncing with Supabase..."
        case .synced: return "Synced to Cloud"
        case .syncFailed: return "Cloud Sync Failed"
        case .signInToSync: return "Sign in to Sync to Cloud"
        }
    }
}

enum AuthState: Equatable {
    case unconfigured
    case signedOut
    case loading
    case signedIn
    case error
}

enum UsernameValidationState {
    case idle
    case checking
    case available
    case taken
    case invalidFormat
    case signInToValidate
    case supabaseNotConfigured
}

enum NfcState: Equatable {
    case unavailable
    case disabled
    case ready
}
