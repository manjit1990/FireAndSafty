package com.yoga.firesafety.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.data.remote.FireSafetyApi
import com.yoga.firesafety.shared.data.remote.dto.RegisterRequest
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignupViewModel(
    private val api: FireSafetyApi,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SignupState>(SignupState.Initial)
    val uiState: StateFlow<SignupState> = _uiState

    fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String?
    ) {
        viewModelScope.launch {
            _uiState.value = SignupState.Loading
            try {
                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    role = Role.TECHNICIAN,
                    phoneNumber = phoneNumber
                )
                val response = api.register(request)
                
                // Save session for auto-login
                sessionRepository.saveSession(email, response.role)
                
                _uiState.value = SignupState.Success(response.role)
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
