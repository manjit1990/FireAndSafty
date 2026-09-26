package com.yoga.firesafety.shared.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yoga.firesafety.shared.db.FireSafetyDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = FireSafetyDatabase.Schema,
            context = context,
            name = "firesafety_v2.db",
            callback = object : AndroidSqliteDriver.Callback(FireSafetyDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                    createTablesIfNotExist(db)
                }
            },
        )
    }

    private fun createTablesIfNotExist(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS WorkOrderEntity (
                id TEXT NOT NULL PRIMARY KEY,
                buildingName TEXT NOT NULL,
                address TEXT NOT NULL,
                type TEXT NOT NULL,
                status TEXT NOT NULL,
                priority TEXT NOT NULL,
                scheduledAt TEXT,
                scheduledEnd TEXT,
                technicianId TEXT,
                technicianName TEXT,
                dispatcherNotes TEXT,
                technicianNotes TEXT,
                isSynced INTEGER DEFAULT 1
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS InspectionEntity (
                id TEXT NOT NULL PRIMARY KEY,
                workOrderId TEXT NOT NULL,
                technicianId TEXT NOT NULL,
                status TEXT NOT NULL,
                startedAt TEXT,
                completedAt TEXT,
                notes TEXT,
                isSynced INTEGER DEFAULT 0
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ChecklistItemEntity (
                id TEXT NOT NULL PRIMARY KEY,
                inspectionId TEXT NOT NULL,
                questionKey TEXT NOT NULL,
                response TEXT,
                observations TEXT,
                isSynced INTEGER DEFAULT 0
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS SessionEntity (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                userId TEXT NOT NULL,
                email TEXT NOT NULL,
                firstName TEXT,
                lastName TEXT,
                role TEXT NOT NULL,
                token TEXT NOT NULL DEFAULT ''
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS TimeEntryEntity (
                id TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL,
                clockInTime INTEGER NOT NULL,
                clockOutTime INTEGER,
                type TEXT NOT NULL,
                workOrderId TEXT,
                workOrderTitle TEXT,
                notes TEXT,
                date TEXT NOT NULL
            );
            """.trimIndent()
        )
    }
}
