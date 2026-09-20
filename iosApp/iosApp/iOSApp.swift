import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        Koin_iosKt.initKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}