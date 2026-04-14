package com.cobertura360.app.data

import android.content.Context
import android.util.Log

class SessionManager(context: Context) {
    
    private val sharedPref = context.getSharedPreferences("cobertura360_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_LOGIN_TIME = "login_time"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun setUserSession(userId: Int, username: String, role: String) {
        try {
            sharedPref.edit().apply {
                putInt(KEY_USER_ID, userId)
                putString(KEY_USERNAME, username)
                putString(KEY_USER_ROLE, role)
                putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
                putBoolean(KEY_IS_LOGGED_IN, true)
                apply()
            }
            Log.d("SessionManager", "Sesión guardada para: $username")
        } catch (e: Exception) {
            Log.e("SessionManager", "Error guardando sesión", e)
        }
    }

    fun getUserId(): Int? {
        return if (isLoggedIn()) sharedPref.getInt(KEY_USER_ID, -1).takeIf { it != -1 } else null
    }

    fun getUsername(): String? {
        return if (isLoggedIn()) sharedPref.getString(KEY_USERNAME, null) else null
    }

    fun getUserRole(): String? {
        return if (isLoggedIn()) sharedPref.getString(KEY_USER_ROLE, null) else null
    }

    fun getLoginTime(): Long {
        return sharedPref.getLong(KEY_LOGIN_TIME, 0L)
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        try {
            sharedPref.edit().clear().apply()
            Log.d("SessionManager", "Sesión finalizada")
        } catch (e: Exception) {
            Log.e("SessionManager", "Error cerrando sesión", e)
        }
    }

    fun clearAll() {
        try {
            sharedPref.edit().clear().apply()
        } catch (e: Exception) {
            Log.e("SessionManager", "Error limpiando sesión", e)
        }
    }
}
