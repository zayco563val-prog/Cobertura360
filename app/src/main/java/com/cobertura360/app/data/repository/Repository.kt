package com.cobertura360.app.data.repository

import android.util.Log
import com.cobertura360.app.data.dao.BaseConfigDao
import com.cobertura360.app.data.dao.SessionDao
import com.cobertura360.app.data.dao.UserDao
import com.cobertura360.app.data.entity.BaseConfigEntity
import com.cobertura360.app.data.entity.SessionEntity
import com.cobertura360.app.data.entity.UserEntity

class UserRepository(private val userDao: UserDao) {
    
    suspend fun loginUser(username: String, password: String): UserEntity? {
        return try {
            val user = userDao.getUserByUsername(username)
            if (user != null && user.password == password && user.isActive) {
                userDao.updateUser(user.copy(lastLogin = System.currentTimeMillis()))
                user
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error en login", e)
            null
        }
    }

    suspend fun getUserById(id: Int): UserEntity? {
        return try {
            userDao.getUserById(id)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error obteniendo usuario", e)
            null
        }
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return try {
            userDao.getUserByUsername(username)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error obteniendo usuario por username", e)
            null
        }
    }

    suspend fun insertDefaultUsers() {
        try {
            val existingAdmin = userDao.getUserByUsername("Admin")
            if (existingAdmin == null) {
                userDao.insertUser(
                    UserEntity(
                        username = "Admin",
                        password = "123456",
                        role = "ADMIN",
                        isActive = true
                    )
                )
                Log.d("UserRepository", "Usuario Admin creado")
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creando usuarios default", e)
        }
    }

    suspend fun changePassword(userId: Int, newPassword: String) {
        try {
            userDao.updateUserPassword(userId, newPassword)
            Log.d("UserRepository", "Contraseña actualizada para usuario $userId")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error cambiando contraseña", e)
        }
    }
}

class BaseConfigRepository(private val baseConfigDao: BaseConfigDao) {
    
    suspend fun getConfig(key: String): String? {
        return try {
            baseConfigDao.getConfigByKey(key)?.configValue
        } catch (e: Exception) {
            Log.e("BaseConfigRepository", "Error obteniendo config", e)
            null
        }
    }

    suspend fun saveConfig(key: String, value: String, updatedBy: String? = null) {
        try {
            val existing = baseConfigDao.getConfigByKey(key)
            if (existing != null) {
                baseConfigDao.updateConfig(
                    existing.copy(
                        configValue = value,
                        updatedAt = System.currentTimeMillis(),
                        updatedBy = updatedBy
                    )
                )
            } else {
                baseConfigDao.insertConfig(
                    BaseConfigEntity(
                        configKey = key,
                        configValue = value,
                        updatedBy = updatedBy
                    )
                )
            }
            Log.d("BaseConfigRepository", "Config guardada: $key")
        } catch (e: Exception) {
            Log.e("BaseConfigRepository", "Error guardando config", e)
        }
    }

    suspend fun getAllConfigs(): Map<String, String> {
        return try {
            baseConfigDao.getAllConfigs().associate { it.configKey to it.configValue }
        } catch (e: Exception) {
            Log.e("BaseConfigRepository", "Error obteniendo configs", e)
            emptyMap()
        }
    }

    suspend fun deleteConfig(key: String) {
        try {
            baseConfigDao.deleteConfigByKey(key)
            Log.d("BaseConfigRepository", "Config eliminada: $key")
        } catch (e: Exception) {
            Log.e("BaseConfigRepository", "Error eliminando config", e)
        }
    }
}

class SessionRepository(private val sessionDao: SessionDao) {
    
    suspend fun createSession(userId: Int): SessionEntity {
        val token = System.currentTimeMillis().toString()
        val session = SessionEntity(
            userId = userId,
            token = token,
            isActive = true
        )
        sessionDao.insertSession(session)
        Log.d("SessionRepository", "Sesión creada para usuario $userId")
        return session
    }

    suspend fun getActiveSession(userId: Int): SessionEntity? {
        return try {
            sessionDao.getActiveSessionByUserId(userId)
        } catch (e: Exception) {
            Log.e("SessionRepository", "Error obteniendo sesión", e)
            null
        }
    }

    suspend fun deactivateSession(userId: Int) {
        try {
            sessionDao.deactivateSessionsByUserId(userId)
            Log.d("SessionRepository", "Sesión desactivada para usuario $userId")
        } catch (e: Exception) {
            Log.e("SessionRepository", "Error desactivando sesión", e)
        }
    }
}
