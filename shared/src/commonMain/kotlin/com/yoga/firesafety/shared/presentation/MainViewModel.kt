package com.yoga.firesafety.shared.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val sessionRepository: SessionRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val session = sessionRepository.getSession()
            if (session != null) {
                _authState.value = AuthState.Authenticated(session)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val session: UserSession) : AuthState()
}
