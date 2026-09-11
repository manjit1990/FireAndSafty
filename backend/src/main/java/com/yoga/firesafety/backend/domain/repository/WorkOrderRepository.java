package com.yoga.firesafety.backend.domain.repository;

import com.yoga.firesafety.backend.domain.entity.User;
import com.yoga.firesafety.backend.domain.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
    List<WorkOrder> findByAssignedTechnician(User technician);
    List<WorkOrder> findByAssignedTechnicianId(UUID technicianId);
}
