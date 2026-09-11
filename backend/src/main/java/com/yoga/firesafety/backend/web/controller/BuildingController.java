package com.yoga.firesafety.backend.web.controller;

import com.yoga.firesafety.backend.domain.entity.Building;
import com.yoga.firesafety.backend.domain.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService service;

    @GetMapping
    public List<Building> getAllBuildings() {
        return service.getAllBuildings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getBuildingById(id));
    }

    @GetMapping("/customer/{customerId}")
    public List<Building> getBuildingsByCustomerId(@PathVariable UUID customerId) {
        return service.getBuildingsByCustomerId(customerId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public Building createBuilding(@Valid @RequestBody Building building) {
        return service.createBuilding(building);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Building> updateBuilding(@PathVariable UUID id, @Valid @RequestBody Building building) {
        return ResponseEntity.ok(service.updateBuilding(id, building));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable UUID id) {
        service.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
