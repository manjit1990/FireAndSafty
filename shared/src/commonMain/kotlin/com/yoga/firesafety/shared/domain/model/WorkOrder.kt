package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class WorkOrder(
    val id: String,
    val buildingName: String,
    val address: String,
    val type: String,
    val status: WorkOrderStatus,
    val priority: String,
    val scheduledAt: String? = null,
    val scheduledEnd: String? = null,
    val technicianName: String? = null,
    val dispatcherNotes: String? = null,
    val technicianNotes: String? = null
)
