import Foundation
import Combine
import UIKit

@MainActor
final class ProfileViewModel: ObservableObject {
    @Published private(set) var profile: Profile = Profile()
    @Published private(set) var profiles: [Profile] = []
    @Published private(set) var syncStatus: SyncStatus = .savedLocally
    @Published private(set) var syncError: String?
    @Published private(set) var nfcState: NfcState = .unavailable
    @Published private(set) var usernameValidationState: UsernameValidationState = .idle
    @Published var nfcResultMessage: String?

    private let store = ProfileStore()
    private let supabase = SupabaseService.shared
    private let nfc = NFCTagService.shared

    private let usernameSubject = PassthroughSubject<String, Never>()
    private var cancellables = Set<AnyCancellable>()

    init() {
        Task { await loadProfiles() }
        checkNfcState()

        usernameSubject
            .debounce(for: .milliseconds(500), scheduler: DispatchQueue.main)
            .removeDuplicates()
            .sink { [weak self] username in
                Task { await self?.validateUsername(username) }
            }
            .store(in: &cancellables)
    }

    func loadProfiles() async {
        let all = await store.loadAll()
        profiles = all
        guard !all.isEmpty else { return }
        let activeId = store.activeProfileId
        let active = all.first { $0.id == activeId } ?? all[0]
        profile = active
        if activeId != active.id {
            store.setActiveProfileId(active.id)
        }
    }

    func checkNfcState() {
        nfcState = nfc.availability
    }

    func switchProfile(id: String) {
        guard let match = profiles.first(where: { $0.id == id }) else { return }
        profile = match
        store.setActiveProfileId(id)
    }

    func createNewProfile(name: String) {
        let newProfile = Profile(profileName: name, profileSlug: Profile.computeSlug(from: name))
        Task {
            await store.save(newProfile)
            store.setActiveProfileId(newProfile.id)
            await loadProfiles()
        }
    }

    func updateProfile(_ updated: Profile) {
        profile = updated
    }

    var shareableURL: String { profile.shareableURL }

    func onUsernameChanged(_ username: String) {
        usernameValidationState = .checking
        usernameSubject.send(username)
    }

    private func validateUsername(_ username: String) async {
        guard !username.isEmpty else {
            usernameValidationState = .idle
            return
        }
        guard username.range(of: "^[a-z0-9-]{3,30}$", options: .regularExpression) != nil else {
            usernameValidationState = .invalidFormat
            return
        }
        guard supabase.isConfigured else {
            usernameValidationState = .supabaseNotConfigured
            return
        }
        guard await supabase.currentSession() != nil else {
            usernameValidationState = .signInToValidate
            return
        }
        do {
            let taken = try await supabase.isUsernameTaken(username)
            if taken && username != profile.username {
                usernameValidationState = .taken
            } else {
                usernameValidationState = .available
            }
        } catch {
            usernameValidationState = .available // allow local save if the network check fails
        }
    }

    /// Saves locally first (always succeeds offline), then syncs to Supabase if signed in -
    /// mirrors SyncProfileRepositoryImpl.saveProfile on Android.
    func saveProfile(profilePhoto: UIImage? = nil, companyLogo: UIImage? = nil) async {
        var toSave = profile
        toSave.isPendingSync = true
        await store.save(toSave)
        syncStatus = .savedLocally
        syncError = nil
        AnalyticsService.log(.cardSaved)

        guard supabase.isConfigured, let session = await supabase.currentSession() else {
            syncStatus = .signInToSync
            return
        }

        do {
            let userId = session.user.id.uuidString

            if let profilePhoto, let data = ImageCompressor.compress(profilePhoto) {
                syncStatus = .uploading
                let url = try await supabase.uploadImage(data, path: "\(userId)/profile-photo.jpg")
                toSave.profilePhotoUrl = url.absoluteString
                AnalyticsService.log(.imageUploaded)
            }
            if let companyLogo, let data = ImageCompressor.compress(companyLogo) {
                syncStatus = .uploading
                let url = try await supabase.uploadImage(data, path: "\(userId)/company-logo.jpg")
                toSave.companyLogoUrl = url.absoluteString
                AnalyticsService.log(.imageUploaded)
            }

            syncStatus = .syncing
            try await supabase.upsertProfile(toSave, userId: userId)

            toSave.isPendingSync = false
            await store.save(toSave)
            profile = toSave
            syncStatus = .synced
            syncError = nil
            AnalyticsService.log(.syncCompleted)
        } catch {
            syncStatus = .syncFailed
            syncError = Self.describeSyncError(error)
        }
    }

    private static func describeSyncError(_ error: Error) -> String {
        let message = error.localizedDescription
        if message.contains("duplicate key value violates unique constraint") {
            return "Username is already taken."
        }
        if message.contains("row-level security") {
            return "RLS Policy failure. Check your Postgres policies."
        }
        if message.contains("bucket") {
            return "Storage bucket 'profile-images' might be missing."
        }
        return "Backend error: \(message)"
    }

    // MARK: - QR

    func generateQRCode() -> UIImage? {
        QRCodeService.generate(content: shareableURL)
    }

    func saveQRToPhotoLibrary(_ image: UIImage) {
        QRCodeService.saveToPhotoLibrary(image)
        AnalyticsService.log(.qrGenerated)
    }

    // MARK: - NFC

    func startNfcProgramming() {
        checkNfcState()
        nfc.startProgramming(url: shareableURL) { [weak self] success, message in
            Task { @MainActor in
                self?.nfcResultMessage = message
                if success {
                    AnalyticsService.log(.nfcProgrammed)
                }
            }
        }
    }

    func stopNfcProgramming() {
        nfc.stop()
    }

    func syncRemoteProfiles() async {
        guard supabase.isConfigured, let session = await supabase.currentSession() else { return }
        let userId = session.user.id.uuidString.lowercased()
        do {
            let remoteProfiles = try await supabase.fetchRemoteProfiles(userId: userId)
            guard !remoteProfiles.isEmpty else { return }
            
            for remote in remoteProfiles {
                await store.save(remote)
            }
            await loadProfiles()
        } catch {
            print("Failed to sync remote profiles: \(error.localizedDescription)")
        }
    }
}
