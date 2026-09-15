package com.yoga.firesafety.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserSession
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                _authState.value = withTimeoutOrNull(SESSION_CHECK_TIMEOUT_MS) {
                    val session = sessionRepository.getSession()
                    val firebaseUser = Firebase.auth.currentUser

                    if (session != null && firebaseUser != null) {
                        AuthState.Authenticated(session)
                    } else {
                        AuthState.Unauthenticated
                    }
                } ?: AuthState.Unauthenticated
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                try {
                    sessionRepository.clearSession()
                } catch (ignored: Exception) {
                    // Best effort cleanup only; opening the app should still continue.
                }
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun logout() {
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
