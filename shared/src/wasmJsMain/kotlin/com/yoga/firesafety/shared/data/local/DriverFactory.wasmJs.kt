package com.yoga.firesafety.shared.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.yoga.firesafety.shared.db.FireSafetyDatabase
import org.w3c.dom.Worker

@JsFun("() => new Worker(new URL('@cashapp/sqldelight-sqljs-worker/index.js', import.meta.url))")
external fun createWorker(): Worker

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(createWorker())
    }
}
