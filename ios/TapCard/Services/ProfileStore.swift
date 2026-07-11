import Foundation

/// Local on-device persistence for profiles - mirrors Room (ProfileDao/ProfileEntity) on Android.
/// Uses a JSON file in the app's Documents directory rather than Core Data/SwiftData to keep the
/// dependency surface small; swap for SwiftData later if the schema grows.
actor ProfileStore {
    private let fileURL: URL
    private let activeProfileKey = "tapcard_active_profile_id"

    init() {
        let documents = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        fileURL = documents.appendingPathComponent("profiles.json")
    }

    func loadAll() -> [Profile] {
        guard let data = try? Data(contentsOf: fileURL) else { return [] }
        return (try? JSONDecoder().decode([Profile].self, from: data)) ?? []
    }

    func save(_ profile: Profile) {
        var all = loadAll()
        if let index = all.firstIndex(where: { $0.id == profile.id }) {
            all[index] = profile
        } else {
            all.append(profile)
        }
        persist(all)
    }

    func delete(id: String) {
        var all = loadAll()
        all.removeAll { $0.id == id }
        persist(all)
    }

    private func persist(_ profiles: [Profile]) {
        guard let data = try? JSONEncoder().encode(profiles) else { return }
        try? data.write(to: fileURL, options: .atomic)
    }

    nonisolated var activeProfileId: String? {
        UserDefaults.standard.string(forKey: activeProfileKey)
    }

    nonisolated func setActiveProfileId(_ id: String) {
        UserDefaults.standard.set(id, forKey: activeProfileKey)
    }
}
