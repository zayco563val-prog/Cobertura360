package com.cobertura360.app.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cobertura360.app.data.SessionManager
import com.cobertura360.app.data.repository.UserRepository
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userRole: String? = null
)

class LoginViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    init {
        _uiState.value = LoginUiState()
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Usuario y contraseña requeridos")
            return
        }

        _uiState.value = LoginUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val user = userRepository.loginUser(username, password)
                if (user != null) {
                    sessionManager.setUserSession(user.id, user.username, user.role)
                    Log.d("LoginViewModel", "Login exitoso: ${user.username}")
                    _uiState.value = LoginUiState(
                        isSuccess = true,
                        userRole = user.role
                    )
                } else {
                    Log.e("LoginViewModel", "Credenciales inválidas")
                    _uiState.value = LoginUiState(error = "Usuario o contraseña incorrecto")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error en login", e)
                _uiState.value = LoginUiState(error = "Error al intentar login: ${e.message}")
            }
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value?.copy(error = null)
    }
}
