package com.learningapp.firesafetyservicemanagement

import android.app.Application
import com.yoga.firesafety.shared.config.initKoin
import com.yoga.firesafety.shared.data.local.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class FireSafetyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FireSafetyApplication)
            modules(module {
                single { DriverFactory(get()) }
            })
        }
    }
}
