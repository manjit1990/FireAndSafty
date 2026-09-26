package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.*

@Serializable
data class EmergencyContact(
    val name: String = "",
    val phoneNumber: String = ""
)

@Serializable
data class AssignedTechnician(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = ""
)

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
    val technicianId: String? = null,
    val technicianName: String? = null,
    val technicianPhoneNumber: String? = null,
    val assignedAt: String? = null,
    val assignedById: String? = null,
    val assignedByName: String? = null,
    val dispatcherNotes: String? = null,
    val technicianNotes: String? = null,
    val completionNotes: String? = null,
    val completionPhotos: List<String> = emptyList(),
    val completionVideos: List<String> = emptyList(),
    val completionQuestions: Map<String, String> = emptyMap(),
    val completedAt: String? = null,
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val assignedTechnicians: List<AssignedTechnician> = emptyList()
)

fun WorkOrder.checkIfOverdue(now: kotlinx.datetime.Instant): Boolean {
    if (status == WorkOrderStatus.COMPLETED || 
        status == WorkOrderStatus.STARTED || 
        status == WorkOrderStatus.IN_PROGRESS) return false
    
    if (scheduledAt.isNullOrBlank()) return false
    
    return try {
        val scheduledInstant = kotlinx.datetime.LocalDateTime.parse(scheduledAt).toInstant(kotlinx.datetime.TimeZone.currentSystemDefault())
        now > scheduledInstant
    } catch (e: Exception) {
        false
    }
}

fun WorkOrder.checkIfCompletionOverdue(now: kotlinx.datetime.Instant): Boolean {
    if (status != WorkOrderStatus.STARTED && 
        status != WorkOrderStatus.IN_PROGRESS) return false
    
    if (scheduledEnd.isNullOrBlank()) return false
    
    return try {
        val endInstant = kotlinx.datetime.LocalDateTime.parse(scheduledEnd).toInstant(kotlinx.datetime.TimeZone.currentSystemDefault())
        now > endInstant
    } catch (e: Exception) {
        false
    }
}
