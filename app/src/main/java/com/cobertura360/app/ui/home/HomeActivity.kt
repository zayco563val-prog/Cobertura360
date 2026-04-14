package com.cobertura360.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cobertura360.app.data.SessionManager
import com.cobertura360.app.data.db.AppDatabase
import com.cobertura360.app.data.repository.BaseConfigRepository
import com.cobertura360.app.data.repository.UserRepository
import com.cobertura360.app.ui.login.LoginActivity
import com.google.android.material.textview.MaterialTextView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var mainLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(20, 20, 20, 20)

        setContentView(mainLayout)

        sessionManager = SessionManager(this)
        initializeViewModel()

        // Observe state
        viewModel.uiState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: HomeUiState) {
        mainLayout.removeAllViews()

        // Title
        val tvTitle = MaterialTextView(this)
        tvTitle.text = "Cobertura360 - Inicio"
        tvTitle.textSize = 24f
        tvTitle.setPadding(0, 0, 0, 20)
        mainLayout.addView(tvTitle)

        // Welcome message
        val tvWelcome = MaterialTextView(this)
        tvWelcome.text = "¡Bienvenido ${state.userName}!\nRol: ${state.userRole}"
        tvWelcome.textSize = 16f
        tvWelcome.setPadding(0, 0, 0, 20)
        mainLayout.addView(tvWelcome)

        // Show error if any
        if (state.error != null) {
            val tvError = MaterialTextView(this)
            tvError.text = "Error: ${state.error}"
            tvError.setTextColor(android.graphics.Color.RED)
            tvError.setPadding(0, 0, 0, 20)
            mainLayout.addView(tvError)
        }

        // Config section (only for ADMIN)
        if (state.userRole == "ADMIN") {
            val tvConfig = MaterialTextView(this)
            tvConfig.text = "\n------- CONFIGURACIÓN BASE -------"
            tvConfig.textSize = 14f
            tvConfig.setPadding(0, 20, 0, 10)
            mainLayout.addView(tvConfig)

            val etConfigKey = EditText(this).apply {
                hint = "Clave de configuración"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 5 }
            }
            mainLayout.addView(etConfigKey)

            val etConfigValue = EditText(this).apply {
                hint = "Valor de configuración"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 10 }
            }
            mainLayout.addView(etConfigValue)

            val btnSaveConfig = Button(this).apply {
                text = "Guardar Configuración"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 20 }
            }
            mainLayout.addView(btnSaveConfig)

            btnSaveConfig.setOnClickListener {
                val key = etConfigKey.text.toString().trim()
                val value = etConfigValue.text.toString().trim()
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    viewModel.saveBaseConfig(key, value)
                    etConfigKey.text.clear()
                    etConfigValue.text.clear()
                    Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            // Show existing configs
            if (state.baseConfig.isNotEmpty()) {
                val tvExistingConfig = MaterialTextView(this)
                tvExistingConfig.text = "\nConfiguración guardada:"
                tvExistingConfig.textSize = 12f
                mainLayout.addView(tvExistingConfig)

                state.baseConfig.forEach { (key, value) ->
                    val tvConfig = MaterialTextView(this)
                    tvConfig.text = "• $key: $value"
                    tvConfig.textSize = 11f
                    mainLayout.addView(tvConfig)
                }
            }
        }

        // Menu based on role
        val tvMenu = MaterialTextView(this)
        tvMenu.text = "\n------- OPCIONES -------"
        tvMenu.textSize = 14f
        tvMenu.setPadding(0, 20, 0, 10)
        mainLayout.addView(tvMenu)

        val menu = viewModel.getNavigationMenuByRole()
        menu.forEach { item ->
            val btnMenu = Button(this).apply {
                text = item.label
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 5 }
            }
            mainLayout.addView(btnMenu)

            btnMenu.setOnClickListener {
                handleMenuAction(item.action)
            }
        }
    }

    private fun handleMenuAction(action: String) {
        when (action) {
            "logout" -> {
                viewModel.logout()
                sessionManager.logout()
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            "change_password" -> {
                Toast.makeText(this, "Cambio de contraseña no implementado aún", Toast.LENGTH_SHORT).show()
            }
            "config" -> {
                Toast.makeText(this, "Sección de configuración disponible arriba", Toast.LENGTH_SHORT).show()
            }
            "users" -> {
                Toast.makeText(this, "Gestión de usuarios no implementada aún", Toast.LENGTH_SHORT).show()
            }
            "home" -> {
                // Already in home
            }
            else -> {
                Toast.makeText(this, "Acción: $action (no implementada)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeViewModel() {
        val db = AppDatabase.getInstance(applicationContext)
        val userRepository = UserRepository(db.userDao())
        val baseConfigRepository = BaseConfigRepository(db.baseConfigDao())

        viewModel = ViewModelProvider(
            this,
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(userRepository, baseConfigRepository, sessionManager) as T
                }
            }
        ).get(HomeViewModel::class.java)
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Usa la opción Logout para cerrar sesión", Toast.LENGTH_SHORT).show()
    }
}
