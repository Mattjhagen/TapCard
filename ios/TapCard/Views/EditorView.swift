import SwiftUI
import PhotosUI

struct EditorView: View {
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    let onBack: () -> Void

    @State private var currentStep = 1

    @State private var fullName = ""
    @State private var jobTitle = ""
    @State private var company = ""
    @State private var phone = ""
    @State private var email = ""
    @State private var website = ""
    @State private var username = ""
    @State private var isDarkTheme = true

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

    private var tempProfile: Profile {
        var base = profileViewModel.profile
        base.fullName = fullName
        base.jobTitle = jobTitle
        base.company = company
        base.phone = phone
        base.email = email
        base.website = website
        base.username = username
        base.isDarkTheme = isDarkTheme
        return base
    }

    var body: some View {
        VStack(spacing: 0) {
            // Progress Bar
            ProgressView(value: Double(currentStep), total: 3.0)
                .tint(.accentColor)
                .padding(.horizontal, 24)
                .padding(.top, 12)

            ScrollView {
                VStack(spacing: 24) {
                    BusinessCardPreview(
                        profile: tempProfile,
                        profilePhotoImage: profilePhotoImage,
                        companyLogoImage: companyLogoImage
                    )
                    .padding(.horizontal, 12)

                    switch currentStep {
                    case 1:
                        identityStep
                    case 2:
                        contactStep
                    case 3:
                        customizeStep
                    default:
                        EmptyView()
                    }
                }
                .padding(24)
            }
        }
        .navigationTitle("Edit Card - Step \(currentStep) of 3")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button("Cancel", action: onBack).fontWeight(.bold)
            }
        }
        .onAppear {
            let profile = profileViewModel.profile
            fullName = profile.fullName
            jobTitle = profile.jobTitle
            company = profile.company
            phone = profile.phone
            email = profile.email
            website = profile.website
            username = profile.username
            isDarkTheme = profile.isDarkTheme
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

    // Step 1: Identity
    private var identityStep: some View {
        VStack(alignment: .leading, spacing: 20) {
            HStack(spacing: 16) {
                PhotosPicker(selection: $profilePhotoItem, matching: .images) {
                    Label(profilePhotoImage == nil ? "Change Photo" : "Photo Added", systemImage: "person.crop.circle")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)

                PhotosPicker(selection: $companyLogoItem, matching: .images) {
                    Label(companyLogoImage == nil ? "Change Logo" : "Logo Added", systemImage: "building.2")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }

            VStack(spacing: 12) {
                TextField("Full Name", text: $fullName)
                TextField("Job Title", text: $jobTitle)
                TextField("Company", text: $company)
            }
            .textFieldStyle(.roundedBorder)

            Spacer()

            Button {
                currentStep = 2
            } label: {
                Text("Next Step")
                    .frame(maxWidth: .infinity)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
            }
            .buttonStyle(.borderedProminent)
            .disabled(fullName.trimmingCharacters(in: .whitespaces).isEmpty)
        }
    }

    // Step 2: Contact
    private var contactStep: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(spacing: 12) {
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

            Spacer()

            Button {
                currentStep = 3
            } label: {
                Text("Next Step")
                    .frame(maxWidth: .infinity)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
            }
            .buttonStyle(.borderedProminent)
            .disabled(phone.isEmpty && email.isEmpty && website.isEmpty)

            Button("Back") {
                currentStep = 1
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 4)
        }
    }

    // Step 3: Customize
    private var customizeStep: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading, spacing: 6) {
                TextField("Username", text: $username)
                    .textFieldStyle(.roundedBorder)
                    .textInputAutocapitalization(.never)
                    .onChange(of: username) { newValue in
                        profileViewModel.onUsernameChanged(newValue)
                    }
                if !helperText.isEmpty {
                    Text(helperText).font(.caption).foregroundStyle(helperColor)
                }
            }

            Toggle("Dark Theme", isOn: $isDarkTheme)
                .padding(.vertical, 8)

            Spacer()

            Button {
                var updated = profileViewModel.profile
                updated.fullName = fullName
                updated.jobTitle = jobTitle
                updated.company = company
                updated.phone = phone
                updated.email = email
                updated.website = website
                updated.username = username
                updated.isDarkTheme = isDarkTheme
                profileViewModel.updateProfile(updated)
                Task {
                    await profileViewModel.saveProfile(profilePhoto: profilePhotoImage, companyLogo: companyLogoImage)
                    onBack()
                }
            } label: {
                Text("Save Changes")
                    .frame(maxWidth: .infinity)
                    .fontWeight(.bold)
                    .padding(.vertical, 12)
            }
            .buttonStyle(.borderedProminent)
            .disabled(!isUsernameValid || username.isEmpty)

            Button("Back") {
                currentStep = 2
            }
            .frame(maxWidth: .infinity)
            .padding(.top, 4)
        }
    }
}
