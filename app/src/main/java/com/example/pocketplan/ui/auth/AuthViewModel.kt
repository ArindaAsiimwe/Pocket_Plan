package com.example.pocketplan.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Simulating a network call
            delay(2000) 
            
            // Keep isLoading true while isSuccess is true to prevent 
            // the spinner from disappearing before navigation happens.
            _uiState.update { it.copy(isSuccess = true) }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Simulating a network call
            delay(2000)
            
            // Keep isLoading true while isSuccess is true
            _uiState.update { it.copy(isSuccess = true) }
        }
    }
    
    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}
