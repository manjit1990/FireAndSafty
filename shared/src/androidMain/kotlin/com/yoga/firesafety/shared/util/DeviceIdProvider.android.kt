package com.yoga.firesafety.shared.util

import android.content.Context
import android.provider.Settings

class AndroidDeviceIdProvider(private val context: Context) : DeviceIdProvider {
    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "android_unknown"
    }
}
