package com.yoga.firesafety.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inspection_items")
@Getter
@Setter
@NoArgsConstructor
public class InspectionItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private Inspection inspection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionItemStatus status;

    @Column(columnDefinition = "TEXT")
    private String observations;

    private boolean repairPerformed = false;

    @Column(columnDefinition = "TEXT")
    private String repairDetails;
}
