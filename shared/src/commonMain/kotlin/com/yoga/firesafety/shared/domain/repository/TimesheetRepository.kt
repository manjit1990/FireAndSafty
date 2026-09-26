package com.yoga.firesafety.shared.domain.repository

import com.yoga.firesafety.shared.domain.model.TimeEntry
import com.yoga.firesafety.shared.domain.model.TimeTrackType
import kotlinx.coroutines.flow.Flow

interface TimesheetRepository {
    suspend fun clockIn(
        userId: String,
        type: TimeTrackType,
        workOrderId: String? = null,
        workOrderTitle: String? = null,
        notes: String? = null
    ): TimeEntry

    suspend fun clockOut(
        entryId: String,
        clockOutTime: Long,
        notes: String? = null
    )

    suspend fun getActiveEntry(userId: String): TimeEntry?
    fun observeActiveEntry(userId: String): Flow<TimeEntry?>
    fun observeAllActiveEntries(): Flow<List<TimeEntry>>
    fun observeTimeEntries(userId: String): Flow<List<TimeEntry>>
    suspend fun getTimeEntriesForDate(userId: String, date: String): List<TimeEntry>
}
