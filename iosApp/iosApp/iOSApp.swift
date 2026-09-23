import SwiftUI
import Shared
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        if let filePath = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: filePath) {
            FirebaseApp.configure(options: options)
            print("[iOSApp] Firebase configured successfully with GoogleService-Info.plist")
        } else if FirebaseApp.app() == nil {
            print("[iOSApp] Warning: GoogleService-Info.plist not found or invalid. Firebase initialization skipped to avoid crash.")
        }

        Koin_iosKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
