package com.yoga.firesafety.shared.data.remote.dto

import com.yoga.firesafety.shared.domain.model.Role
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: Role,
    val phoneNumber: String? = null
)

@Serializable
data class AuthenticationRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthenticationResponse(
    val token: String,
    val role: Role,
    val firstName: String? = null,
    val lastName: String? = null
)
