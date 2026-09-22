import SwiftUI
import Shared
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        Koin_iosKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
