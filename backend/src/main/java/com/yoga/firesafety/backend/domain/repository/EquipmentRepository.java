package com.yoga.firesafety.backend.domain.repository;

import com.yoga.firesafety.backend.domain.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    List<Equipment> findByBuildingId(UUID buildingId);
}
