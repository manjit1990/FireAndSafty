package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.domain.model.AssignedTechnician
import com.yoga.firesafety.shared.domain.model.EmergencyContact
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseWorkOrderRepository : WorkOrderRepository {
    private val firestore = Firebase.firestore
    private val collection = firestore.collection("work_orders")

    override fun getWorkOrders(): Flow<List<WorkOrder>> {
        return collection.snapshots.map { querySnapshot ->
            querySnapshot.documents.map { doc -> doc.data<WorkOrder>() }
        }
    }

    override suspend fun refreshWorkOrders() {
        // Firestore handles caching and syncing automatically
    }

    override suspend fun updateStatus(id: String, status: WorkOrderStatus) {
        collection.document(id).update("status" to status.name)
    }

    override suspend fun createWorkOrder(order: WorkOrder) {
        collection.document(order.id).set(order)
    }

    override suspend fun assignWorkOrder(
        id: String,
        technicianId: String,
        technicianName: String,
        technicianPhoneNumber: String?,
        scheduledAt: String?,
        scheduledEnd: String?,
        assignedAt: String,
        assignedById: String?,
        assignedByName: String?,
        emergencyContacts: List<EmergencyContact>,
        assignedTechnicians: List<AssignedTechnician>
    ) {
        collection.document(id).update(
            "status" to WorkOrderStatus.ASSIGNED.name,
            "technicianId" to technicianId,
            "technicianName" to technicianName,
            "technicianPhoneNumber" to technicianPhoneNumber,
            "scheduledAt" to scheduledAt,
            "scheduledEnd" to scheduledEnd,
            "assignedAt" to assignedAt,
            "assignedById" to assignedById,
            "assignedByName" to assignedByName,
            "emergencyContacts" to emergencyContacts,
            "assignedTechnicians" to assignedTechnicians
        )
    }

    override fun getWorkOrdersForTechnician(technicianId: String): Flow<List<WorkOrder>> {
        return collection.snapshots.map { querySnapshot ->
            querySnapshot.documents.map { doc -> doc.data<WorkOrder>() }.filter { order ->
                order.technicianId == technicianId || order.assignedTechnicians.any { it.id == technicianId }
            }
        }
    }

    override suspend fun startVisit(orderId: String) {
        collection.document(orderId).update("status" to WorkOrderStatus.STARTED.name)
    }

    override suspend fun completeVisit(
        orderId: String,
        notes: String?,
        photos: List<String>,
        videos: List<String>,
        questions: Map<String, String>,
        completedAt: String
    ) {
        collection.document(orderId).update(
            "status" to WorkOrderStatus.COMPLETED.name,
            "completionNotes" to notes,
            "completionPhotos" to photos,
            "completionVideos" to videos,
            "completionQuestions" to questions,
            "completedAt" to completedAt
        )
    }
}
