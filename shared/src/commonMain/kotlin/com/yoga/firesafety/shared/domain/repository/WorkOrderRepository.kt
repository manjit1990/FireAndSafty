package com.yoga.firesafety.shared.domain.repository

import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import kotlinx.coroutines.flow.Flow

interface WorkOrderRepository {
    fun getWorkOrders(): Flow<List<WorkOrder>>
    suspend fun refreshWorkOrders()
    suspend fun updateStatus(id: String, status: WorkOrderStatus)
    suspend fun createWorkOrder(order: WorkOrder)
    suspend fun assignWorkOrder(
        id: String,
        technicianId: String,
        technicianName: String,
        scheduledAt: String?,
        scheduledEnd: String?,
        assignedAt: String,
        assignedById: String?,
        assignedByName: String?
    )
    fun getWorkOrdersForTechnician(technicianId: String): Flow<List<WorkOrder>>
    suspend fun startVisit(orderId: String)
    suspend fun completeVisit(
        orderId: String,
        notes: String?,
        photos: List<String>,
        videos: List<String>,
        questions: Map<String, String>,
        completedAt: String
    )
}
