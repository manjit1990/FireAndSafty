package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val phoneNumber: String? = null,
    val isActive: Boolean = true
)
