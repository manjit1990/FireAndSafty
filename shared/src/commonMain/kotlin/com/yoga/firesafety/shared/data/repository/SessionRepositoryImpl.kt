package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.db.FireSafetyDatabase
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserSession

class SessionRepositoryImpl(database: FireSafetyDatabase) : SessionRepository {
    private val queries = database.fireSafetyDatabaseQueries

    override suspend fun getSession(): UserSession? {
        val entity = queries.getSession().executeAsOneOrNull()
        return entity?.let {
            UserSession(it.email, Role.valueOf(it.role), it.token)
        }
    }

    override suspend fun saveSession(email: String, role: Role, token: String) {
        queries.saveSession(email, role.name, token)
    }

    override suspend fun clearSession() {
        queries.clearSession()
    }
}
