package com.learningapp.firesafetyservicemanagement

import android.app.Application
import android.content.Intent
import com.yoga.firesafety.shared.config.initKoin
import com.yoga.firesafety.shared.data.local.DriverFactory
import com.yoga.firesafety.shared.util.DeviceIdProvider
import com.yoga.firesafety.shared.util.AndroidDeviceIdProvider
import com.yoga.firesafety.shared.util.NotificationService
import com.yoga.firesafety.shared.util.AndroidNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.android.inject
import org.koin.dsl.module

class FireSafetyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FireSafetyApplication)
            modules(module {
                single { DriverFactory(get()) }
                single<DeviceIdProvider> { AndroidDeviceIdProvider(get()) }
                single<NotificationService> { AndroidNotificationService(get()) }
            })
        }

        val notificationService: NotificationService by inject()
        notificationService.init()

        // Start checking for overdue tasks
        sendBroadcast(Intent(this, OverdueTaskReceiver::class.java))
    }
}
