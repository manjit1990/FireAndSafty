package com.yoga.firesafety.shared.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.User
import com.yoga.firesafety.shared.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                userRepository.getAllUsers().collect {
                    _users.value = it
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                val rawMsg = e.message ?: "Failed to load users"
                val displayMsg = if (rawMsg.contains("Access Denied")) {
                    "Access Denied: You do not have Admin privileges."
                } else {
                    rawMsg
                }
                _error.value = displayMsg
                _isLoading.value = false
            }
        }
    }

    fun promoteToAdmin(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.updateUserRole(userId, Role.ADMIN)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
