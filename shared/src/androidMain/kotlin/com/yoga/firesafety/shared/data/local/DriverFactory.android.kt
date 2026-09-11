package com.yoga.firesafety.shared.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yoga.firesafety.shared.db.FireSafetyDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(FireSafetyDatabase.Schema, context, "firesafety_v2.db")
    }
}
