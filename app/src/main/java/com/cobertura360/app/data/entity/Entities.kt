package com.cobertura360.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val role: String, // ADMIN, SUPERVISOR, ENCUESTADOR
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long? = null
)

@Entity(tableName = "base_config")
data class BaseConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val configKey: String,
    val configValue: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val updatedBy: String? = null
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val token: String,
    val loginTime: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
