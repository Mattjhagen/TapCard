import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var authViewModel: AuthViewModel
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    let onBack: () -> Void

    var body: some View {
        List {
            Section("Account") {
                LabeledContent("Profile Sync Status", value: profileViewModel.syncStatus.label)
                if authViewModel.authState == .signedIn {
                    Button("Sign Out", role: .destructive) {
                        Task {
                            await authViewModel.signOut()
                            onBack()
                        }
                    }
                }
            }

            Section("App Details") {
                LabeledContent("About TapCard", value: "Version \(appVersion)")
                Text("Privacy Policy")
                Text("Open Source Licenses")
            }
        }
        .navigationTitle("Settings")
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button("Back", action: onBack)
            }
        }
    }

    private var appVersion: String {
        let short = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
        let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "1"
        return "\(short) (\(build))"
    }
}
