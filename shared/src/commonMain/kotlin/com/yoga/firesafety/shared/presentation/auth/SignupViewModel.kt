package com.yoga.firesafety.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import com.yoga.firesafety.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SignupState>(SignupState.Initial)
    val uiState: StateFlow<SignupState> = _uiState

    fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _uiState.value = SignupState.Loading
            try {
                // For testing/demo: If email contains "admin", register as ADMIN
                val role = if (email.lowercase().contains("admin")) Role.ADMIN else Role.TECHNICIAN
                
                val user = userRepository.register(firstName, lastName, email, password, role, phoneNumber)
                
                // Save session for auto-login
                sessionRepository.saveSession(user.id, email, user.firstName, user.lastName, user.role, "firebase_token")
                
                _uiState.value = SignupState.Success(user.role)
            } catch (e: Exception) {
                _uiState.value = SignupState.Error(e.message ?: "Registration failed")
            }
        }
    }
}

sealed class SignupState {
    object Initial : SignupState()
    object Loading : SignupState()
    data class Success(val role: Role) : SignupState()
    data class Error(val message: String) : SignupState()
}
