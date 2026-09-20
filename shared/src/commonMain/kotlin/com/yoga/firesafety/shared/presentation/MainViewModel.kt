package com.yoga.firesafety.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserRepository
import com.yoga.firesafety.shared.domain.repository.UserSession
import com.yoga.firesafety.shared.util.DeviceIdProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainViewModel(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState
    
    private var monitoringJob: Job? = null

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val currentSession = withTimeoutOrNull(SESSION_CHECK_TIMEOUT_MS) {
                    val session = sessionRepository.getSession()
                    val firebaseUser = Firebase.auth.currentUser

                    if (session != null && firebaseUser != null) {
                        session
                    } else {
                        null
                    }
                }

                if (currentSession != null) {
                    _authState.value = AuthState.Authenticated(currentSession)
                    startMonitoring(currentSession.userId)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                try {
                    sessionRepository.clearSession()
                } catch (ignored: Exception) {
                }
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    private fun startMonitoring(userId: String) {
        monitoringJob?.cancel()
        monitoringJob = viewModelScope.launch {
            val currentDeviceId = deviceIdProvider.getDeviceId()
            userRepository.observeUser(userId)
                .filterNotNull()
                .collect { user ->
                    // If deviceId exists in DB and doesn't match current device, force logout
                    if (user.deviceId != null && user.deviceId != currentDeviceId) {
                        logout()
                    }
                }
        }
    }

    fun logout() {
        monitoringJob?.cancel()
        viewModelScope.launch {
            Firebase.auth.signOut()
            sessionRepository.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
    }

    private companion object {
        const val SESSION_CHECK_TIMEOUT_MS = 2_000L
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val session: UserSession) : AuthState()
}
