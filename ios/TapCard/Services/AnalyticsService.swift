import Foundation

/// Analytics event names - mirrors AnalyticsManager.kt on Android. No Firebase SDK wired up yet;
/// swap the `log` implementation for FirebaseAnalytics.logEvent(_:parameters:) once
/// GoogleService-Info.plist is added, matching the Android app's google-services.json setup.
enum AnalyticsService {
    static func log(_ event: Event) {
        #if DEBUG
        print("[Analytics] \(event.rawValue)")
        #endif
    }

    enum Event: String {
        case accountCreated = "account_created"
        case cardSaved = "card_saved"
        case qrGenerated = "qr_generated"
        case qrShared = "qr_shared"
        case nfcProgrammed = "nfc_programmed"
        case imageUploaded = "image_uploaded"
        case syncCompleted = "sync_completed"
        case walletButtonTapped = "wallet_button_tapped"
    }
}
