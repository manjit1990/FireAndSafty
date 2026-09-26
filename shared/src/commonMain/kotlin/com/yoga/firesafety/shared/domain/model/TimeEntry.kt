package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TimeTrackType {
    VISIT,
    GENERAL
}

@Serializable
data class TimeEntry(
    val id: String,
    val userId: String,
    val clockInTime: Long,
    val clockOutTime: Long? = null,
    val type: TimeTrackType,
    val workOrderId: String? = null,
    val workOrderTitle: String? = null,
    val notes: String? = null,
    val date: String
) {
    val durationMinutes: Long
        get() {
            val endTime = clockOutTime ?: return 0L
            val durationMs = endTime - clockInTime
            return if (durationMs > 0) durationMs / (1000 * 60) else 0L
        }
}
