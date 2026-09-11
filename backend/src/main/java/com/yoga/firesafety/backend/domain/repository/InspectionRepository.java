package com.yoga.firesafety.backend.domain.repository;

import com.yoga.firesafety.backend.domain.entity.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, UUID> {
    Optional<Inspection> findByWorkOrderId(UUID workOrderId);
}
