package com.yoga.firesafety.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Initial)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                val user = userRepository.login(email, password)
                
                // For demo: Force ADMIN role if email contains "admin"
                val effectiveRole = if (email.lowercase().contains("admin")) Role.ADMIN else user.role
                
                // Save session for auto-login (Token is handled by Firebase Auth, but we cache role/name)
                sessionRepository.saveSession(user.id, email, user.firstName, user.lastName, effectiveRole, "firebase_token")
                
                _uiState.value = LoginState.Success(effectiveRole)
            } catch (e: Exception) {
                // Printing the full error to help debugging
                println("Login Error Details: ${e.message}")
                val friendlyMessage = when {
                    e.message?.contains("user-not-found") == true -> "No user found with this email."
                    e.message?.contains("wrong-password") == true -> "Incorrect password. Please try again."
                    e.message?.contains("network-request-failed") == true -> "Network error. Check your internet."
                    else -> e.message ?: "Authentication failed"
                }
                _uiState.value = LoginState.Error(friendlyMessage)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.clearSession()
        }
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val role: Role) : LoginState()
    data class Error(val message: String) : LoginState()
}
