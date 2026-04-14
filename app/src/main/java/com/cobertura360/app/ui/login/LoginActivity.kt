package com.cobertura360.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cobertura360.app.data.SessionManager
import com.cobertura360.app.data.db.AppDatabase
import com.cobertura360.app.data.repository.UserRepository
import com.cobertura360.app.ui.home.HomeActivity
import com.google.android.material.textview.MaterialTextView

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvStatus: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create simple UI
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 20)

        // Title
        tvStatus = MaterialTextView(this)
        tvStatus.text = "Cobertura360 - Login"
        tvStatus.textSize = 24f
        tvStatus.setPadding(0, 0, 0, 20)
        layout.addView(tvStatus)

        // Username
        etUsername = EditText(this)
        etUsername.hint = "Usuario"
        etUsername.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 10 }
        layout.addView(etUsername)

        // Password
        etPassword = EditText(this)
        etPassword.hint = "Contraseña"
        etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        etPassword.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 20 }
        layout.addView(etPassword)

        // Login Button
        btnLogin = Button(this)
        btnLogin.text = "Ingresar"
        btnLogin.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layout.addView(btnLogin)

        // Status text
        val tvHint = MaterialTextView(this)
        tvHint.text = "\nPrueba:\nUsuario: Admin\nContraseña: 123456"
        tvHint.textSize = 12f
        tvHint.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = 20 }
        layout.addView(tvHint)

        setContentView(layout)

        // Initialize ViewModel
        initializeViewModel()

        // Setup listeners
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.login(username, password)
        }

        // Observe state
        viewModel.uiState.observe(this) { state ->
            when {
                state.isLoading -> {
                    btnLogin.isEnabled = false
                    btnLogin.text = "Ingresando..."
                }
                state.isSuccess -> {
                    Toast.makeText(this, "¡Bienvenido ${etUsername.text}!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                state.error != null -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Ingresar"
                    Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                }
                else -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Ingresar"
                }
            }
        }
    }

    private fun initializeViewModel() {
        val db = AppDatabase.getInstance(applicationContext)
        val userRepository = UserRepository(db.userDao())
        val sessionManager = SessionManager(this)

        viewModel = ViewModelProvider(
            this,
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(userRepository, sessionManager) as T
                }
            }
        ).get(LoginViewModel::class.java)
    }
}
