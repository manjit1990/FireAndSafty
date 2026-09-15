package com.yoga.firesafety.shared.domain.repository

import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun register(firstName: String, lastName: String, email: String, password: String, role: Role, phoneNumber: String): User
    suspend fun login(email: String, password: String): User
    fun getAllUsers(): Flow<List<User>>
    suspend fun updateUserRole(userId: String, role: Role)
    suspend fun getCurrentUser(): User?
    suspend fun updateUserProfile(userId: String, firstName: String, lastName: String, phoneNumber: String, profileImageUrl: String?)
}
