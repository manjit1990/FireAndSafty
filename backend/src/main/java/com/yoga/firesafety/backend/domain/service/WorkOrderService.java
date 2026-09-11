package com.yoga.firesafety.backend.domain.service;

import com.yoga.firesafety.backend.domain.entity.WorkOrder;
import com.yoga.firesafety.backend.domain.entity.WorkOrderStatus;
import com.yoga.firesafety.backend.domain.repository.WorkOrderRepository;
import com.yoga.firesafety.backend.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository repository;

    public List<WorkOrder> getAllWorkOrders() {
        return repository.findAll();
    }

    public WorkOrder getWorkOrderById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found with id: " + id));
    }

    public List<WorkOrder> getWorkOrdersByTechnician(UUID technicianId) {
        return repository.findByAssignedTechnicianId(technicianId);
    }

    @Transactional
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        workOrder.setStatus(WorkOrderStatus.NEW);
        return repository.save(workOrder);
    }

    @Transactional
    public WorkOrder assignWorkOrder(UUID id, UUID technicianId) {
        WorkOrder workOrder = getWorkOrderById(id);
        // Business Rule: Only NEW or ASSIGNED jobs can be (re)assigned
        if (workOrder.getStatus() != WorkOrderStatus.NEW && workOrder.getStatus() != WorkOrderStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot assign work order in status: " + workOrder.getStatus());
        }
        
        // In a real app, we'd fetch the technician user and set it
        // workOrder.setAssignedTechnician(technician);
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        return repository.save(workOrder);
    }

    @Transactional
    public WorkOrder updateStatus(UUID id, WorkOrderStatus newStatus) {
        WorkOrder workOrder = getWorkOrderById(id);
        validateStatusTransition(workOrder.getStatus(), newStatus);
        
        workOrder.setStatus(newStatus);
        
        if (newStatus == WorkOrderStatus.IN_PROGRESS && workOrder.getStartedAt() == null) {
            workOrder.setStartedAt(LocalDateTime.now());
        } else if (newStatus == WorkOrderStatus.COMPLETED) {
            workOrder.setCompletedAt(LocalDateTime.now());
        }
        
        return repository.save(workOrder);
    }

    private void validateStatusTransition(WorkOrderStatus current, WorkOrderStatus next) {
        // Define valid transitions as requested
        boolean valid = switch (current) {
            case NEW -> next == WorkOrderStatus.ASSIGNED || next == WorkOrderStatus.CANCELLED;
            case ASSIGNED -> next == WorkOrderStatus.ACCEPTED || next == WorkOrderStatus.ASSIGNED || next == WorkOrderStatus.CANCELLED;
            case ACCEPTED -> next == WorkOrderStatus.EN_ROUTE || next == WorkOrderStatus.CANCELLED;
            case EN_ROUTE -> next == WorkOrderStatus.ON_SITE || next == WorkOrderStatus.CANCELLED;
            case ON_SITE -> next == WorkOrderStatus.IN_PROGRESS || next == WorkOrderStatus.CANCELLED;
            case IN_PROGRESS -> next == WorkOrderStatus.COMPLETED || next == WorkOrderStatus.REPAIR_REQUIRED || next == WorkOrderStatus.CANCELLED;
            case REPAIR_REQUIRED -> next == WorkOrderStatus.COMPLETED || next == WorkOrderStatus.IN_PROGRESS || next == WorkOrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false; // Final states
        };
        
        if (!valid) {
            throw new IllegalStateException("Invalid status transition from " + current + " to " + next);
        }
    }
}
