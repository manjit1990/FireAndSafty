package com.yoga.firesafety.shared.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WorkOrderViewModel(private val repository: WorkOrderRepository) : ViewModel() {
    private val _assignmentState = MutableStateFlow<AssignmentState>(AssignmentState.Idle)
    val assignmentState: StateFlow<AssignmentState> = _assignmentState

    private val _technicianId = MutableStateFlow<String?>(null)
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val technicianWorkOrders: StateFlow<List<WorkOrder>> = _technicianId
        .filterNotNull()
        .flatMapLatest { id -> repository.getWorkOrdersForTechnician(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workOrders: StateFlow<List<WorkOrder>> = repository.getWorkOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTechnicianFilter(id: String) {
        _technicianId.value = id
    }

    fun getWorkOrdersForTechnician(technicianId: String): StateFlow<List<WorkOrder>> {
        setTechnicianFilter(technicianId)
        return technicianWorkOrders
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshWorkOrders()
        }
    }

    fun createOrder(order: WorkOrder) {
        viewModelScope.launch {
            repository.createWorkOrder(order)
        }
    }

    fun assignOrder(
        orderId: String,
        technicianId: String,
        technicianName: String,
        scheduledAt: String?,
        scheduledEnd: String?,
        assignedAt: String,
        assignedById: String?,
        assignedByName: String?
    ) {
        viewModelScope.launch {
            _assignmentState.value = AssignmentState.Loading
            try {
                repository.assignWorkOrder(
                    id = orderId,
                    technicianId = technicianId,
                    technicianName = technicianName,
                    scheduledAt = scheduledAt,
                    scheduledEnd = scheduledEnd,
                    assignedAt = assignedAt,
                    assignedById = assignedById,
                    assignedByName = assignedByName
                )
                _assignmentState.value = AssignmentState.Success
            } catch (e: Exception) {
                _assignmentState.value = AssignmentState.Error(e.message ?: "Unable to assign work order")
            }
        }
    }

    fun clearAssignmentState() {
        _assignmentState.value = AssignmentState.Idle
    }

    fun startVisit(orderId: String) {
        viewModelScope.launch {
            try {
                repository.startVisit(orderId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun completeVisit(
        orderId: String,
        notes: String?,
        photos: List<String>,
        videos: List<String>,
        questions: Map<String, String>,
        completedAt: String
    ) {
        viewModelScope.launch {
            try {
                repository.completeVisit(orderId, notes, photos, videos, questions, completedAt)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class AssignmentState {
    object Idle : AssignmentState()
    object Loading : AssignmentState()
    object Success : AssignmentState()
    data class Error(val message: String) : AssignmentState()
}
