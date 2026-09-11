package com.yoga.firesafety.backend.web.controller;

import com.yoga.firesafety.backend.domain.entity.WorkOrder;
import com.yoga.firesafety.backend.domain.entity.WorkOrderStatus;
import com.yoga.firesafety.backend.domain.service.WorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService service;

    @GetMapping
    public List<WorkOrder> getAllWorkOrders() {
        return service.getAllWorkOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> getWorkOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getWorkOrderById(id));
    }

    @GetMapping("/my-jobs")
    public List<WorkOrder> getMyWorkOrders(@RequestAttribute("userId") UUID userId) {
        return service.getWorkOrdersByTechnician(userId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public WorkOrder createWorkOrder(@Valid @RequestBody WorkOrder workOrder) {
        return service.createWorkOrder(workOrder);
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<WorkOrder> assignWorkOrder(@PathVariable UUID id, @RequestParam UUID technicianId) {
        return ResponseEntity.ok(service.assignWorkOrder(id, technicianId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrder> updateStatus(@PathVariable UUID id, @RequestParam WorkOrderStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
