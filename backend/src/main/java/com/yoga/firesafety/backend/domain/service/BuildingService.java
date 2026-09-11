package com.yoga.firesafety.backend.domain.service;

import com.yoga.firesafety.backend.domain.entity.Building;
import com.yoga.firesafety.backend.domain.repository.BuildingRepository;
import com.yoga.firesafety.backend.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository repository;

    public List<Building> getAllBuildings() {
        return repository.findAll();
    }

    public List<Building> getBuildingsByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Building getBuildingById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
    }

    @Transactional
    public Building createBuilding(Building building) {
        return repository.save(building);
    }

    @Transactional
    public Building updateBuilding(UUID id, Building buildingDetails) {
        Building building = getBuildingById(id);
        building.setName(buildingDetails.getName());
        building.setAddress(buildingDetails.getAddress());
        building.setLatitude(buildingDetails.getLatitude());
        building.setLongitude(buildingDetails.getLongitude());
        building.setBuildingType(buildingDetails.getBuildingType());
        building.setNotes(buildingDetails.getNotes());
        return repository.save(building);
    }

    @Transactional
    public void deleteBuilding(UUID id) {
        Building building = getBuildingById(id);
        repository.delete(building);
    }
}
