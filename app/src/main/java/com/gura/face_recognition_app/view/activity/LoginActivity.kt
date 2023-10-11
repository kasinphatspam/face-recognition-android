package com.gura.face_recognition_app.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.RegisterActivity
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.LoginActivityViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    private lateinit var viewModel: LoginActivityViewModel
    private lateinit var factory: AppViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[LoginActivityViewModel::class.java]

        // Initialize the layout variable with view id
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                viewModel.login(email,password)
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        viewModel.authCmd.observe(this){
            if(it.cmd == "AUTH_LOGIN_COMPLETED"){
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }else if(it.cmd == "AUTH_LOGIN_FAILED"){
                Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show()
            }
        }
    }
}