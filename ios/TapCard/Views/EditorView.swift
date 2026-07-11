import SwiftUI
import PhotosUI

struct EditorView: View {
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    let onBack: () -> Void

    @State private var username: String = ""
    @State private var profilePhotoItem: PhotosPickerItem?
    @State private var profilePhotoImage: UIImage?
    @State private var companyLogoItem: PhotosPickerItem?
    @State private var companyLogoImage: UIImage?

    private var isUsernameValid: Bool {
        switch profileViewModel.usernameValidationState {
        case .available, .signInToValidate, .supabaseNotConfigured, .idle: return true
        default: return false
        }
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                BusinessCardPreview(
                    profile: profileViewModel.profile,
                    profilePhotoImage: profilePhotoImage,
                    companyLogoImage: companyLogoImage
                )

                Toggle("Dark Theme", isOn: Binding(
                    get: { profileViewModel.profile.isDarkTheme },
                    set: { newValue in
                        var updated = profileViewModel.profile
                        updated.isDarkTheme = newValue
                        profileViewModel.updateProfile(updated)
                    }
                ))
                .padding(.top, 8)

                HStack(spacing: 12) {
                    PhotosPicker(selection: $profilePhotoItem, matching: .images) {
                        Text("Change Photo")
                    }
                    PhotosPicker(selection: $companyLogoItem, matching: .images) {
                        Text("Change Logo")
                    }
                }
                .buttonStyle(.bordered)

                VStack(alignment: .leading, spacing: 4) {
                    TextField("Username (for share link)", text: $username)
                        .textFieldStyle(.roundedBorder)
                        .textInputAutocapitalization(.never)
                        .onChange(of: username) { newValue in
                            profileViewModel.onUsernameChanged(newValue)
                        }
                    if !helperText.isEmpty {
                        Text(helperText).font(.caption).foregroundStyle(helperColor)
                    }
                }

                Button {
                    var updated = profileViewModel.profile
                    updated.username = username
                    profileViewModel.updateProfile(updated)
                    Task {
                        await profileViewModel.saveProfile(profilePhoto: profilePhotoImage, companyLogo: companyLogoImage)
                        onBack()
                    }
                } label: {
                    Text("Save Changes").frame(maxWidth: .infinity).fontWeight(.bold)
                }
                .buttonStyle(.borderedProminent)
                .disabled(!isUsernameValid)
            }
            .padding(24)
        }
        .navigationTitle("Card Editor")
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button("Back", action: onBack).fontWeight(.bold)
            }
        }
        .onAppear {
            username = profileViewModel.profile.username
            profileViewModel.onUsernameChanged(username)
        }
        .onChange(of: profilePhotoItem) { newItem in
            Task {
                if let data = try? await newItem?.loadTransferable(type: Data.self) {
                    profilePhotoImage = UIImage(data: data)
                }
            }
        }
        .onChange(of: companyLogoItem) { newItem in
            Task {
                if let data = try? await newItem?.loadTransferable(type: Data.self) {
                    companyLogoImage = UIImage(data: data)
                }
            }
        }
    }

    private var helperText: String {
        switch profileViewModel.usernameValidationState {
        case .idle: return ""
        case .checking: return "Checking availability..."
        case .available: return "Username available"
        case .taken: return "Username taken"
        case .invalidFormat: return "Invalid format (3-30 chars, lowercase, numbers, hyphens)"
        case .signInToValidate: return "Local only / username not reserved (sign in to sync)"
        case .supabaseNotConfigured: return "Local only (Supabase not configured)"
        }
    }

    private var helperColor: Color {
        switch profileViewModel.usernameValidationState {
        case .available: return .green
        case .taken, .invalidFormat: return .red
        default: return .secondary
        }
    }
}
