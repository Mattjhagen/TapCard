import SwiftUI
import PhotosUI

struct OnboardingView: View {
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    let onComplete: () -> Void

    @State private var fullName = ""
    @State private var jobTitle = ""
    @State private var company = ""
    @State private var phone = ""
    @State private var email = ""
    @State private var website = ""
    @State private var username = ""

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

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Let's set up your digital business card.")
                    .foregroundStyle(.secondary)

                HStack {
                    PhotosPicker(selection: $profilePhotoItem, matching: .images) {
                        Label(profilePhotoImage == nil ? "Add Photo" : "Photo Selected", systemImage: "person.crop.circle")
                    }
                    .buttonStyle(.bordered)

                    PhotosPicker(selection: $companyLogoItem, matching: .images) {
                        Label(companyLogoImage == nil ? "Add Logo" : "Logo Selected", systemImage: "building.2")
                    }
                    .buttonStyle(.bordered)
                }

                Group {
                    TextField("Full Name", text: $fullName)
                    TextField("Job Title", text: $jobTitle)
                    TextField("Company", text: $company)
                    TextField("Phone Number", text: $phone)
                        .keyboardType(.phonePad)
                    TextField("Email Address", text: $email)
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                    TextField("Website", text: $website)
                        .keyboardType(.URL)
                        .textInputAutocapitalization(.never)
                }
                .textFieldStyle(.roundedBorder)

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
                    updated.fullName = fullName
                    updated.jobTitle = jobTitle
                    updated.company = company
                    updated.phone = phone
                    updated.email = email
                    updated.website = website
                    updated.username = username
                    profileViewModel.updateProfile(updated)
                    Task {
                        await profileViewModel.saveProfile(profilePhoto: profilePhotoImage, companyLogo: companyLogoImage)
                        onComplete()
                    }
                } label: {
                    Text("Create Card")
                        .frame(maxWidth: .infinity)
                        .fontWeight(.bold)
                }
                .buttonStyle(.borderedProminent)
                .disabled(!isUsernameValid)
                .padding(.top, 8)
            }
            .padding(24)
        }
        .navigationTitle("Create Profile")
        .onAppear { profileViewModel.onUsernameChanged(username) }
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
}
