package com.cobertura360.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cobertura360.app.data.SessionManager
import com.cobertura360.app.data.db.AppDatabase
import com.cobertura360.app.data.repository.UserRepository
import com.cobertura360.app.ui.home.HomeActivity
import com.cobertura360.app.ui.login.LoginActivity
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show splash UI
        val textView = MaterialTextView(this)
        textView.text = "Cobertura360\nCargando..."
        textView.textSize = 20f
        setContentView(textView)

        // Initialize app data
        initializeApp()
    }

    private fun initializeApp() {
        lifecycleScope.launch {
            try {
                Log.d("SplashActivity", "Inicializando aplicación")

                // Initialize database
                val db = AppDatabase.getInstance(applicationContext)
                val userRepository = UserRepository(db.userDao())

                // Insert default admin user if doesn't exist
                userRepository.insertDefaultUsers()
                Log.d("SplashActivity", "Base de datos inicializada")

                // Check if user is logged in
                val sessionManager = SessionManager(applicationContext)
                val isLoggedIn = sessionManager.isLoggedIn()

                Thread.sleep(1000) // Small delay for UX

                if (isLoggedIn) {
                    Log.d("SplashActivity", "Usuario ya está logueado, yendo a Home")
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                } else {
                    Log.d("SplashActivity", "Usuario no logueado, yendo a Login")
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()

            } catch (e: Exception) {
                Log.e("SplashActivity", "Error en la inicialización", e)
                // Fallback to login screen on error
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
