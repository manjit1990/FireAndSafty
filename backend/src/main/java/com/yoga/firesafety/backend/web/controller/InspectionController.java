package com.yoga.firesafety.backend.web.controller;

import com.yoga.firesafety.backend.domain.entity.Inspection;
import com.yoga.firesafety.backend.domain.service.InspectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService service;

    @GetMapping("/work-order/{workOrderId}")
    public ResponseEntity<Inspection> getInspectionByWorkOrder(@PathVariable UUID workOrderId) {
        return ResponseEntity.ok(service.getInspectionByWorkOrder(workOrderId));
    }

    @PostMapping
    public ResponseEntity<Inspection> submitInspection(@Valid @RequestBody Inspection inspection) {
        return ResponseEntity.ok(service.submitInspection(inspection));
    }
}
