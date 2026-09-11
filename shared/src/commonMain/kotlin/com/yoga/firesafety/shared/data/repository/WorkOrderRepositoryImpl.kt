package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.data.remote.FireSafetyApi
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import com.yoga.firesafety.shared.db.FireSafetyDatabase
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkOrderRepositoryImpl(
    private val api: FireSafetyApi,
    private val database: FireSafetyDatabase
) : WorkOrderRepository {

    private val queries = database.fireSafetyDatabaseQueries

    override fun getWorkOrders(): Flow<List<WorkOrder>> {
        return queries.selectAllWorkOrders()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    WorkOrder(
                        id = entity.id,
                        buildingName = entity.buildingName,
                        address = entity.address,
                        type = entity.type,
                        status = WorkOrderStatus.valueOf(entity.status),
                        priority = entity.priority,
                        scheduledAt = entity.scheduledAt,
                        scheduledEnd = entity.scheduledEnd,
                        technicianName = entity.technicianName,
                        dispatcherNotes = entity.dispatcherNotes,
                        technicianNotes = entity.technicianNotes
                    )
                }
            }
    }

    override suspend fun refreshWorkOrders() {
        val remoteOrders = api.getWorkOrders()
        database.transaction {
            queries.clearWorkOrders()
            remoteOrders.forEach { order ->
                queries.insertWorkOrder(
                    id = order.id,
                    buildingName = order.buildingName,
                    address = order.address,
                    type = order.type,
                    status = order.status.name,
                    priority = order.priority,
                    scheduledAt = order.scheduledAt,
                    scheduledEnd = order.scheduledEnd,
                    technicianName = order.technicianName,
                    dispatcherNotes = order.dispatcherNotes,
                    technicianNotes = order.technicianNotes,
                    isSynced = true
                )
            }
        }
    }

    override suspend fun updateStatus(id: String, status: WorkOrderStatus) {
        queries.updateWorkOrderStatus(status.name, id)
        try {
            api.updateWorkOrderStatus(id, status)
            queries.updateWorkOrderStatus(status.name, id) // Mark as synced conceptually if we had a separate flag
        } catch (e: Exception) {
            // Log error, job will remain with isSynced = false in DB
        }
    }

    override suspend fun createWorkOrder(order: WorkOrder) {
        queries.insertWorkOrder(
            id = order.id,
            buildingName = order.buildingName,
            address = order.address,
            type = order.type,
            status = order.status.name,
            priority = order.priority,
            scheduledAt = order.scheduledAt,
            scheduledEnd = order.scheduledEnd,
            technicianName = order.technicianName,
            dispatcherNotes = order.dispatcherNotes,
            technicianNotes = order.technicianNotes,
            isSynced = false // Conceptual: locally created, not yet on server
        )
    }

    override suspend fun assignWorkOrder(id: String, technicianId: String, scheduledAt: String?, scheduledEnd: String?) {
        val updatedOrder = api.assignWorkOrder(id, technicianId, scheduledAt, scheduledEnd)
        // Update local database
        queries.insertWorkOrder(
            id = updatedOrder.id,
            buildingName = updatedOrder.buildingName,
            address = updatedOrder.address,
            type = updatedOrder.type,
            status = updatedOrder.status.name,
            priority = updatedOrder.priority,
            scheduledAt = updatedOrder.scheduledAt,
            scheduledEnd = updatedOrder.scheduledEnd,
            technicianName = updatedOrder.technicianName,
            dispatcherNotes = updatedOrder.dispatcherNotes,
            technicianNotes = updatedOrder.technicianNotes,
            isSynced = true
        )
    }
}
