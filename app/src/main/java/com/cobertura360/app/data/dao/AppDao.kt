package com.cobertura360.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cobertura360.app.data.entity.BaseConfigEntity
import com.cobertura360.app.data.entity.SessionEntity
import com.cobertura360.app.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username AND isActive = 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updateUserPassword(userId: Int, newPassword: String)
}

@Dao
interface BaseConfigDao {
    @Insert
    suspend fun insertConfig(config: BaseConfigEntity): Long

    @Update
    suspend fun updateConfig(config: BaseConfigEntity)

    @Query("SELECT * FROM base_config WHERE configKey = :key")
    suspend fun getConfigByKey(key: String): BaseConfigEntity?

    @Query("SELECT * FROM base_config")
    suspend fun getAllConfigs(): List<BaseConfigEntity>

    @Query("DELETE FROM base_config WHERE configKey = :key")
    suspend fun deleteConfigByKey(key: String)
}

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Update
    suspend fun updateSession(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE userId = :userId AND isActive = 1 ORDER BY loginTime DESC LIMIT 1")
    suspend fun getActiveSessionByUserId(userId: Int): SessionEntity?

    @Query("UPDATE sessions SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateSessionsByUserId(userId: Int)

    @Query("UPDATE sessions SET isActive = 0 WHERE id = :sessionId")
    suspend fun deactivateSession(sessionId: Int)
}
