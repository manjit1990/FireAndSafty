package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.db.FireSafetyDatabase
import com.yoga.firesafety.shared.db.TimeEntryEntity
import com.yoga.firesafety.shared.domain.model.TimeEntry
import com.yoga.firesafety.shared.domain.model.TimeTrackType
import com.yoga.firesafety.shared.domain.repository.TimesheetRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TimesheetRepositoryImpl(database: FireSafetyDatabase) : TimesheetRepository {
    private val queries = database.fireSafetyDatabaseQueries

    override suspend fun clockIn(
        userId: String,
        type: TimeTrackType,
        workOrderId: String?,
        workOrderTitle: String?,
        notes: String?
    ): TimeEntry = withContext(Dispatchers.IO) {
        val nowMs = Clock.System.now().toEpochMilliseconds()
        val dateStr = getTodayDateString(nowMs)
        val id = "TE-" + nowMs

        queries.insertTimeEntry(
            id = id,
            userId = userId,
            clockInTime = nowMs,
            clockOutTime = null,
            type = type.name,
            workOrderId = workOrderId,
            workOrderTitle = workOrderTitle,
            notes = notes,
            date = dateStr
        )

        TimeEntry(
            id = id,
            userId = userId,
            clockInTime = nowMs,
            clockOutTime = null,
            type = type,
            workOrderId = workOrderId,
            workOrderTitle = workOrderTitle,
            notes = notes,
            date = dateStr
        )
    }

    override suspend fun clockOut(entryId: String, clockOutTime: Long, notes: String?) {
        withContext(Dispatchers.IO) {
            queries.updateClockOutWithNotes(
                clockOutTime = clockOutTime,
                notes = notes,
                id = entryId
            )
        }
    }

    override suspend fun getActiveEntry(userId: String): TimeEntry? = withContext(Dispatchers.IO) {
        queries.getActiveTimeEntry(userId).executeAsOneOrNull()?.toDomain()
    }

    override fun observeActiveEntry(userId: String): Flow<TimeEntry?> {
        return queries.getActiveTimeEntry(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity -> entity?.toDomain() }
    }

    override fun observeAllActiveEntries(): Flow<List<TimeEntry>> {
        return queries.getAllActiveTimeEntries()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeTimeEntries(userId: String): Flow<List<TimeEntry>> {
        return queries.getTimeEntriesForUser(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTimeEntriesForDate(userId: String, date: String): List<TimeEntry> = withContext(Dispatchers.IO) {
        queries.getTimeEntriesForUserAndDate(userId, date).executeAsList().map { it.toDomain() }
    }

    private fun TimeEntryEntity.toDomain(): TimeEntry {
        val trackType = try {
            TimeTrackType.valueOf(type)
        } catch (e: Exception) {
            TimeTrackType.GENERAL
        }
        return TimeEntry(
            id = id,
            userId = userId,
            clockInTime = clockInTime,
            clockOutTime = clockOutTime,
            type = trackType,
            workOrderId = workOrderId,
            workOrderTitle = workOrderTitle,
            notes = notes,
            date = date
        )
    }

    private fun getTodayDateString(epochMs: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMs)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return localDate.toString() // YYYY-MM-DD
    }
}
