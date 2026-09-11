package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class WorkOrderStatus {
    NEW,
    ASSIGNED,
    ACCEPTED,
    EN_ROUTE,
    ON_SITE,
    IN_PROGRESS,
    REPAIR_REQUIRED,
    COMPLETED,
    CANCELLED
}
