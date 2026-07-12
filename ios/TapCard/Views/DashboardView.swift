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
        ZStack {
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
                        let profile = profileViewModel.profile
                        let username = profile.username.isEmpty ? profile.id : profile.username
                        let slug = profile.profileSlug.isEmpty ? Profile.computeSlug(from: profile.profileName) : profile.profileSlug
                        let walletUrl = "\(AppConfig.baseURL)/api/wallet/apple/\(username)/\(slug)"
                        if let url = URL(string: walletUrl) {
                            UIApplication.shared.open(url)
                        }
                    } label: {
                        Text("Add to Apple Wallet")
                            .frame(maxWidth: .infinity)
                            .fontWeight(.semibold)
                    }
                    .buttonStyle(.bordered)
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

            if isProgrammingNfc {
                nfcProgrammingOverlay
            }
        }
    }

    private var nfcButtonText: String {
        switch profileViewModel.nfcState {
        case .unavailable: return "NFC Unavailable"
        case .disabled: return "NFC Disabled (Enable in Settings)"
        case .ready: return isProgrammingNfc ? "Ready to Program (Hold Tag Near)" : "Program NFC Tag"
        }
    }

    private var nfcProgrammingOverlay: some View {
        ZStack {
            Color.black.opacity(0.85)
                .ignoresSafeArea()
                .onTapGesture {} // Block underlying interactions

            VStack(spacing: 40) {
                Spacer().frame(height: 40)

                VStack(spacing: 16) {
                    PulsingNfcIcon()
                    
                    Text("Ready to Program")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundStyle(.white)
                    
                    Text("Hold your iPhone near the physical NFC tag.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                }

                // Frosted glass business card preview
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        Text("TAP CARD WRITER")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .foregroundStyle(.secondary)
                        Spacer()
                        Text("WAITING FOR TAG")
                            .font(.caption2)
                            .fontWeight(.bold)
                            .foregroundStyle(.yellow)
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(profileViewModel.profile.fullName.isEmpty ? "New Identity" : profileViewModel.profile.fullName)
                            .font(.title2)
                            .fontWeight(.bold)
                            .foregroundStyle(.white)
                        Text(profileViewModel.profile.jobTitle.isEmpty ? "Digital Business Card" : "\(profileViewModel.profile.jobTitle) @ \(profileViewModel.profile.company)")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                }
                .padding(24)
                .frame(maxWidth: .infinity)
                .frame(height: 180)
                .background(.ultraThinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                .overlay(
                    RoundedRectangle(cornerRadius: 24, style: .continuous)
                        .stroke(.white.opacity(0.15), lineWidth: 1)
                )
                .padding(.horizontal, 24)

                Spacer()

                Button {
                    profileViewModel.stopNfcProgramming()
                    isProgrammingNfc = false
                } label: {
                    Image(systemName: "xmark")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundStyle(.white)
                        .frame(width: 56, height: 56)
                        .background(.white.opacity(0.15))
                        .clipShape(Circle())
                }

                Spacer().frame(height: 24)
            }
        }
    }
}

struct PulsingNfcIcon: View {
    @State private var isPulsing = false
    
    var body: some View {
        Image(systemName: "antenna.radiowaves.left.and.right")
            .font(.system(size: 82))
            .foregroundStyle(.white)
            .scaleEffect(isPulsing ? 1.15 : 0.85)
            .animation(.easeInOut(duration: 1.2).repeatForever(autoreverses: true), value: isPulsing)
            .onAppear {
                isPulsing = true
            }
    }
}

extension SyncStatus {
    var isFailure: Bool { if case .syncFailed = self { return true }; return false }
}
