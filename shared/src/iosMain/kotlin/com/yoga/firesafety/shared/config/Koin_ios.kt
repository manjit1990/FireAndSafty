@file:OptIn(ExperimentalObjCName::class)
package com.yoga.firesafety.shared.config

import com.yoga.firesafety.shared.data.local.DriverFactory
import com.yoga.firesafety.shared.util.DeviceIdProvider
import com.yoga.firesafety.shared.util.IosDeviceIdProvider
import com.yoga.firesafety.shared.util.NotificationService
import com.yoga.firesafety.shared.util.IosNotificationService
import org.koin.dsl.module
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@ObjCName("startKoinIos")
fun startKoinIos() {
    initKoin {
        modules(module {
            single { DriverFactory() }
            single<DeviceIdProvider> { IosDeviceIdProvider() }
            single<NotificationService> { IosNotificationService() }
        })
    }
}
