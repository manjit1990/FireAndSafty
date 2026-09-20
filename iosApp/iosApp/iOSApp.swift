import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        Koin_iosKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}