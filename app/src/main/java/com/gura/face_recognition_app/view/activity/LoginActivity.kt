package com.gura.face_recognition_app.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.LoginActivityViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    // Declare ViewModel and ViewModelFactory
    private lateinit var viewModel: LoginActivityViewModel
    private lateinit var factory: AppViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize helper for customizing display components
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        // Initialize ViewModel and ViewModelFactory
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[LoginActivityViewModel::class.java]

        // Initialize UI elements with view IDs
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Use a coroutine for login operation
            lifecycleScope.launch {
                viewModel.login(email, password)
            }
        }

        // Handle register button click
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Observe authentication commands from the ViewModel
        viewModel.authCmd.observe(this) { authCommand ->
            when (authCommand.cmd) {
                "AUTH_LOGIN_COMPLETED" -> {
                    // If login is successful, navigate to the main activity
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                "AUTH_LOGIN_FAILED" -> {
                    // If login fails, show a toast message
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
