package com.example.pocketplan.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketplan.data.model.User
import com.example.pocketplan.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isSessionChecked: Boolean = false,
    val user: User? = null  // 👈 added
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val user = authRepository.restoreSession()
            if (user != null) {
                _uiState.update { it.copy(isSuccess = true, isSessionChecked = true, user = user) }
            } else {
                _uiState.update { it.copy(isSessionChecked = true) }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(email, pass)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, isSuccess = true, user = user) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Login failed") }
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.register(name, email, pass)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, isSuccess = true, user = user) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Registration failed") }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.sendPasswordReset(email)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = "Reset email sent! Check your inbox") }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.updateName(newName)
            result.onSuccess {
                _uiState.update { it.copy(
                    isLoading = false,
                    user = it.user?.copy(name = newName),
                    error = "Name updated successfully"
                )}
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to update name") }
            }
        }
    }

    fun updatePassword(newPassword: String) {
        if (newPassword.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.updatePassword(newPassword)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = "Password updated successfully") }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to update password") }
            }
        }
    }

    fun updateProfilePicture(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.updateProfilePicture(path)
            result.onSuccess {
                _uiState.update { it.copy(
                    isLoading = false,
                    user = it.user?.copy(profilePicPath = path)
                )}
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to update picture") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { AuthUiState(isSessionChecked = true) }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(error = null, isSuccess = false) }
    }
}