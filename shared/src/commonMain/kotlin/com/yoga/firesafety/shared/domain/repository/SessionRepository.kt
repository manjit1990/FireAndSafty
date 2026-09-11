package com.yoga.firesafety.shared.domain.repository

import com.yoga.firesafety.shared.domain.model.Role
import kotlinx.coroutines.flow.Flow

data class UserSession(val email: String, val role: Role)

interface SessionRepository {
    suspend fun getSession(): UserSession?
    suspend fun saveSession(email: String, role: Role)
    suspend fun clearSession()
}
