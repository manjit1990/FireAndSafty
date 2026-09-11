package com.yoga.firesafety.backend.domain.service;

import com.yoga.firesafety.backend.domain.entity.Inspection;
import com.yoga.firesafety.backend.domain.entity.WorkOrder;
import com.yoga.firesafety.backend.domain.entity.WorkOrderStatus;
import com.yoga.firesafety.backend.domain.repository.InspectionRepository;
import com.yoga.firesafety.backend.domain.repository.WorkOrderRepository;
import com.yoga.firesafety.backend.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository repository;
    private final WorkOrderRepository workOrderRepository;

    public Inspection getInspectionByWorkOrder(UUID workOrderId) {
        return repository.findByWorkOrderId(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspection not found for work order: " + workOrderId));
    }

    @Transactional
    public Inspection submitInspection(Inspection inspection) {
        WorkOrder workOrder = workOrderRepository.findById(inspection.getWorkOrder().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found"));
        
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Work order must be IN_PROGRESS to submit inspection");
        }
        
        inspection.setCompletedAt(LocalDateTime.now());
        
        // Link items to the inspection if not already linked
        if (inspection.getItems() != null) {
            inspection.getItems().forEach(item -> item.setInspection(inspection));
        }
        
        Inspection savedInspection = repository.save(inspection);
        
        // Update work order status based on inspection results
        boolean hasFailedItems = inspection.getItems().stream()
                .anyMatch(item -> item.getStatus() == com.yoga.firesafety.backend.domain.entity.InspectionItemStatus.FAIL);
        
        if (hasFailedItems) {
            workOrder.setStatus(WorkOrderStatus.REPAIR_REQUIRED);
        } else {
            workOrder.setStatus(WorkOrderStatus.COMPLETED);
        }
        workOrder.setCompletedAt(LocalDateTime.now());
        workOrderRepository.save(workOrder);
        
        return savedInspection;
    }
}
