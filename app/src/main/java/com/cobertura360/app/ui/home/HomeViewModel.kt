package com.cobertura360.app.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cobertura360.app.data.SessionManager
import com.cobertura360.app.data.repository.BaseConfigRepository
import com.cobertura360.app.data.repository.UserRepository
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "",
    val userRole: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val baseConfig: Map<String, String> = emptyMap(),
    val configUpdated: Boolean = false
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val baseConfigRepository: BaseConfigRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    init {
        val userName = sessionManager.getUsername() ?: "Desconocido"
        val userRole = sessionManager.getUserRole() ?: "GUEST"
        _uiState.value = HomeUiState(userName = userName, userRole = userRole)
        loadConfigs()
    }

    private fun loadConfigs() {
        viewModelScope.launch {
            try {
                val configs = baseConfigRepository.getAllConfigs()
                _uiState.value = _uiState.value?.copy(baseConfig = configs)
                Log.d("HomeViewModel", "Configs cargados: ${configs.size}")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error cargando configs", e)
                _uiState.value = _uiState.value?.copy(error = "Error cargando configuración")
            }
        }
    }

    fun saveBaseConfig(key: String, value: String) {
        if (userRole() != "ADMIN") {
            _uiState.value = _uiState.value?.copy(error = "No tienes permisos para editar configuración")
            return
        }

        _uiState.value = _uiState.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                baseConfigRepository.saveConfig(key, value, sessionManager.getUsername())
                loadConfigs()
                _uiState.value = _uiState.value?.copy(
                    configUpdated = true,
                    isLoading = false
                )
                Log.d("HomeViewModel", "Config guardada: $key")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error guardando config", e)
                _uiState.value = _uiState.value?.copy(
                    error = "Error guardando config",
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    // Deactivate session in DB
                }
                sessionManager.logout()
                Log.d("HomeViewModel", "Logout exitoso")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error en logout", e)
            }
        }
    }

    fun getNavigationMenuByRole(): List<MenuItem> {
        return when (userRole()) {
            "ADMIN" -> listOf(
                MenuItem("Inicio", "home"),
                MenuItem("Configuración", "config"),
                MenuItem("Usuarios", "users"),
                MenuItem("Cambiar Contraseña", "change_password"),
                MenuItem("Logout", "logout")
            )
            "SUPERVISOR" -> listOf(
                MenuItem("Inicio", "home"),
                MenuItem("Reportes", "reports"),
                MenuItem("Cambiar Contraseña", "change_password"),
                MenuItem("Logout", "logout")
            )
            "ENCUESTADOR" -> listOf(
                MenuItem("Inicio", "home"),
                MenuItem("Encuestas", "surveys"),
                MenuItem("Cambiar Contraseña", "change_password"),
                MenuItem("Logout", "logout")
            )
            else -> listOf(
                MenuItem("Logout", "logout")
            )
        }
    }

    private fun userRole(): String = _uiState.value?.userRole ?: "GUEST"

    fun resetConfigUpdated() {
        _uiState.value = _uiState.value?.copy(configUpdated = false)
    }

    fun resetError() {
        _uiState.value = _uiState.value?.copy(error = null)
    }

    data class MenuItem(val label: String, val action: String)
}
