package com.yoga.firesafety.shared.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.yoga.firesafety.shared.db.FireSafetyDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(FireSafetyDatabase.Schema, "firesafety.db")
    }
}
