package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    ADMIN,
    DISPATCHER,
    TECHNICIAN,
    MANAGER
}
