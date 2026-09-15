package com.yoga.firesafety.shared.domain.repository

import com.yoga.firesafety.shared.domain.model.Role
import kotlinx.coroutines.flow.Flow

data class UserSession(
    val userId: String,
    val email: String, 
    val firstName: String?, 
    val lastName: String?, 
    val role: Role, 
    val token: String
)

interface SessionRepository {
    suspend fun getSession(): UserSession?
    suspend fun saveSession(userId: String, email: String, firstName: String?, lastName: String?, role: Role, token: String)
    suspend fun clearSession()
}
