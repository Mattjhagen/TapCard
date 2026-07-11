import SwiftUI

struct DashboardView: View {
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    @State private var showSettings = false
    @State private var showEditor = false
    @State private var qrImage: UIImage?
    @State private var shareItems: [Any]?
    @State private var isProgrammingNfc = false
    @State private var toastMessage: String?

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                BusinessCardPreview(profile: profileViewModel.profile)
                    .padding(.horizontal, 24)

                Text(profileViewModel.syncStatus.label)
                    .font(.footnote)
                    .foregroundStyle(profileViewModel.syncStatus.isFailure ? .red : .secondary)

                if profileViewModel.syncStatus.isFailure, let error = profileViewModel.syncError {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Sync Error").font(.headline)
                        Text(error).font(.subheadline)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding()
                    .background(Color.red.opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 24)
                }

                VStack(spacing: 16) {
                    Text("Share via QR Code").font(.headline)
                    if let qrImage {
                        Image(uiImage: qrImage)
                            .interpolation(.none)
                            .resizable()
                            .frame(width: 200, height: 200)
                    }

                    HStack(spacing: 12) {
                        Button("Share QR") {
                            if let qrImage {
                                shareItems = [qrImage, "Here is my digital business card: \(profileViewModel.shareableURL)"]
                            }
                        }
                        Button("Save QR") {
                            if let qrImage {
                                profileViewModel.saveQRToPhotoLibrary(qrImage)
                                toastMessage = "Saved to Photos"
                            }
                        }
                        Button("Copy Link") {
                            UIPasteboard.general.string = profileViewModel.shareableURL
                            toastMessage = "Link copied!"
                        }
                    }
                    .buttonStyle(.bordered)
                }

                Button {
                    if let fileURL = VCardService.writeTempFile(for: profileViewModel.profile) {
                        shareItems = [fileURL]
                    }
                } label: {
                    Label("Share Contact Card (AirDrop)", systemImage: "square.and.arrow.up")
                        .frame(maxWidth: .infinity)
                        .fontWeight(.semibold)
                }
                .buttonStyle(.borderedProminent)
                .padding(.horizontal, 24)

                Button {
                    if isProgrammingNfc {
                        profileViewModel.stopNfcProgramming()
                        isProgrammingNfc = false
                    } else {
                        profileViewModel.startNfcProgramming()
                        isProgrammingNfc = true
                    }
                } label: {
                    Text(nfcButtonText)
                        .frame(maxWidth: .infinity)
                        .fontWeight(.bold)
                }
                .buttonStyle(.borderedProminent)
                .tint(isProgrammingNfc ? .accentColor : .gray)
                .disabled(profileViewModel.nfcState != .ready)
                .padding(.horizontal, 24)

                Button {
                    toastMessage = "Apple Wallet coming soon"
                } label: {
                    Text("Add to Apple Wallet (coming soon)")
                        .frame(maxWidth: .infinity)
                        .fontWeight(.semibold)
                }
                .buttonStyle(.bordered)
                .disabled(true)
                .padding(.horizontal, 24)
            }
            .padding(.vertical, 16)
        }
        .navigationTitle(profileViewModel.profile.profileName)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Menu {
                    ForEach(profileViewModel.profiles) { p in
                        Button(p.profileName) { profileViewModel.switchProfile(id: p.id) }
                    }
                    Divider()
                    Button("+ Create New Identity") {
                        profileViewModel.createNewProfile(name: "Identity \(profileViewModel.profiles.count + 1)")
                    }
                } label: {
                    HStack {
                        Text(profileViewModel.profile.profileName).fontWeight(.bold)
                        Image(systemName: "chevron.down")
                    }
                }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Edit") { showEditor = true }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Button {
                    showSettings = true
                } label: {
                    Image(systemName: "gearshape")
                }
            }
        }
        .onAppear {
            profileViewModel.checkNfcState()
            qrImage = profileViewModel.generateQRCode()
        }
        .onChange(of: profileViewModel.shareableURL) { _ in
            qrImage = profileViewModel.generateQRCode()
        }
        .onChange(of: profileViewModel.nfcResultMessage) { message in
            guard let message else { return }
            toastMessage = message
            isProgrammingNfc = false
        }
        .sheet(isPresented: Binding(get: { shareItems != nil }, set: { if !$0 { shareItems = nil } })) {
            if let shareItems {
                ShareSheet(items: shareItems)
            }
        }
        .sheet(isPresented: $showEditor) {
            NavigationStack { EditorView(onBack: { showEditor = false }) }
        }
        .sheet(isPresented: $showSettings) {
            NavigationStack { SettingsView(onBack: { showSettings = false }) }
        }
        .alert(toastMessage ?? "", isPresented: Binding(get: { toastMessage != nil }, set: { if !$0 { toastMessage = nil } })) {
            Button("OK", role: .cancel) {}
        }
    }

    private var nfcButtonText: String {
        switch profileViewModel.nfcState {
        case .unavailable: return "NFC Unavailable"
        case .disabled: return "NFC Disabled (Enable in Settings)"
        case .ready: return isProgrammingNfc ? "Ready to Program (Hold Tag Near)" : "Program NFC Tag"
        }
    }
}

extension SyncStatus {
    var isFailure: Bool { if case .syncFailed = self { return true }; return false }
}
