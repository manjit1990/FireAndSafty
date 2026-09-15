package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.db.FireSafetyDatabase
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SessionRepositoryImpl(database: FireSafetyDatabase) : SessionRepository {
    private val queries = database.fireSafetyDatabaseQueries

    override suspend fun getSession(): UserSession? = withContext(Dispatchers.IO) {
        val entity = queries.getSession().executeAsOneOrNull()
        if (entity == null) return@withContext null

        val role = Role.entries.firstOrNull { it.name.equals(entity.role, ignoreCase = true) }
            ?: return@withContext null

        UserSession(entity.userId, entity.email, entity.firstName, entity.lastName, role, entity.token)
    }

    override suspend fun saveSession(userId: String, email: String, firstName: String?, lastName: String?, role: Role, token: String) {
        withContext(Dispatchers.IO) {
            queries.saveSession(userId, email, firstName, lastName, role.name, token)
        }
    }

    override suspend fun clearSession() {
        withContext(Dispatchers.IO) {
            queries.clearSession()
        }
    }
}
