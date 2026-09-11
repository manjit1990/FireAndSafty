package com.yoga.firesafety.shared.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.data.remote.FireSafetyApi
import com.yoga.firesafety.shared.domain.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val api: FireSafetyApi) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Initial)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                // In a real app, call api.authenticate and store token
                // For demo, simulating role based success
                val role = if (email.contains("admin")) Role.ADMIN else Role.TECHNICIAN
                _uiState.value = LoginState.Success(role)
            } catch (e: Exception) {
                _uiState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val role: Role) : LoginState()
    data class Error(val message: String) : LoginState()
}
