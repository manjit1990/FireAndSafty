package com.yoga.firesafety.backend.domain.repository;

import com.yoga.firesafety.backend.domain.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<Building, UUID> {
    List<Building> findByCustomerId(UUID customerId);
}
