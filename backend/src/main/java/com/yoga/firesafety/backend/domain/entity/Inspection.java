package com.yoga.firesafety.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "inspections")
@Getter
@Setter
@NoArgsConstructor
public class Inspection extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String customerSignatureUrl;

    @Column(columnDefinition = "TEXT")
    private String generalNotes;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL)
    private List<InspectionItem> items;
}
