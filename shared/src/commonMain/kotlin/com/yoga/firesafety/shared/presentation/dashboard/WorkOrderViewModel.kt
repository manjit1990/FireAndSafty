package com.yoga.firesafety.shared.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.repository.WorkOrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkOrderViewModel(private val repository: WorkOrderRepository) : ViewModel() {

    val workOrders: StateFlow<List<WorkOrder>> = repository.getWorkOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}
