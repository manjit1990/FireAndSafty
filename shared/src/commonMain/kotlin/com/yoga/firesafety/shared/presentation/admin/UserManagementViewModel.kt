package com.yoga.firesafety.shared.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoga.firesafety.shared.data.remote.FireSafetyApi
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(private val api: FireSafetyApi) : ViewModel() {
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
                _users.value = api.getAllUsers()
            } catch (e: Exception) {
                val rawMsg = e.message ?: "Failed to load users"
                val displayMsg = if (rawMsg.contains("Access Denied")) {
                    "Access Denied: You do not have Admin privileges on the server. Please register a new account with 'admin' in the email."
                } else {
                    rawMsg
                }
                _error.value = displayMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun promoteToAdmin(userId: String) {
        viewModelScope.launch {
            try {
                api.updateUserRole(userId, Role.ADMIN)
                loadUsers() // Refresh list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
