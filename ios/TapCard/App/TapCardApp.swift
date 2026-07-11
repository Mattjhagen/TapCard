import SwiftUI

@main
struct TapCardApp: App {
    @StateObject private var authViewModel = AuthViewModel()
    @StateObject private var profileViewModel = ProfileViewModel()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(authViewModel)
                .environmentObject(profileViewModel)
        }
    }
}

private enum AppRoute {
    case auth
    case onboarding
    case dashboard
}

struct RootView: View {
    @EnvironmentObject private var profileViewModel: ProfileViewModel
    @State private var route: AppRoute = .auth

    var body: some View {
        NavigationStack {
            switch route {
            case .auth:
                AuthView(onAuthSuccess: {
                    route = profileViewModel.profiles.isEmpty ? .onboarding : .dashboard
                })
            case .onboarding:
                OnboardingView(onComplete: { route = .dashboard })
            case .dashboard:
                DashboardView()
            }
        }
    }
}
