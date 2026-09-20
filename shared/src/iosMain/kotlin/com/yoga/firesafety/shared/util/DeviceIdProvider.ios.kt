package com.yoga.firesafety.shared.util

import platform.UIKit.UIDevice

class IosDeviceIdProvider : DeviceIdProvider {
    override fun getDeviceId(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "ios_unknown"
    }
}
