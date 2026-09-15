package com.yoga.firesafety.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val phoneNumber: String,
    val isAdmin: Boolean = false,
    val isActive: Boolean = true,
    val profileImageUrl: String? = null
)
