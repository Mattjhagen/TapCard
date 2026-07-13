import SwiftUI
import AuthenticationServices

struct AuthView: View {
    @EnvironmentObject private var authViewModel: AuthViewModel
    let onAuthSuccess: () -> Void

    @State private var email = ""
    @State private var password = ""

    var body: some View {
        VStack(spacing: 16) {
            Spacer()

            switch authViewModel.authState {
            case .unconfigured:
                Text("Supabase not configured.")
                    .font(.headline)
                    .foregroundStyle(.red)
                Button("Continue offline", action: onAuthSuccess)
                    .buttonStyle(.borderedProminent)

            case .loading:
                ProgressView()

            case .error:
                Text(authViewModel.errorMessage ?? "Authentication failed. Please try again.")
                    .foregroundStyle(.red)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 16)
                Button("Retry") {
                    Task { await authViewModel.signOut() }
                }
                .buttonStyle(.bordered)

            case .signedIn:
                ProgressView().onAppear(perform: onAuthSuccess)

            case .signedOut:
                Text("TapCard Authentication")
                    .font(.title2.bold())

                TextField("Email", text: $email)
                    .textFieldStyle(.roundedBorder)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.emailAddress)

                SecureField("Password", text: $password)
                    .textFieldStyle(.roundedBorder)

                Button("Sign In") {
                    Task { await authViewModel.signIn(email: email, password: password) }
                }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)

                Button("Sign Up") {
                    Task { await authViewModel.signUp(email: email, password: password) }
                }
                .buttonStyle(.bordered)
                .frame(maxWidth: .infinity)

                Divider()
                    .padding(.vertical, 8)

                SignInWithAppleButton(.signIn) { request in
                    request.requestedScopes = [.email, .fullName]
                } onCompletion: { result in
                    switch result {
                    case .success(let authorization):
                        Task {
                            await authViewModel.signInWithApple(authorization: authorization)
                        }
                    case .failure(let error):
                        authViewModel.errorMessage = error.localizedDescription
                        // Show error screen
                        Task {
                            await authViewModel.signOut()
                        }
                    }
                }
                .signInWithAppleButtonStyle(.black)
                .frame(height: 44)
                .frame(maxWidth: .infinity)

                Button("Skip for now", action: onAuthSuccess)
                    .buttonStyle(.plain)
                    .foregroundStyle(.secondary)
                    .padding(.top, 8)
            }

            Spacer()
        }
        .padding(24)
        .onChange(of: authViewModel.authState) { newValue in
            if newValue == .signedIn { onAuthSuccess() }
        }
    }
}
