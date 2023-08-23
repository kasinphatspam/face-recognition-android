package com.gura.face_recognition_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.service.AuthService
import com.gura.face_recognition_app.viewmodel.LoginActivityViewModel
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize helper for customizing display component
        val displayComponentHelper = DisplayComponentHelper(this@LoginActivity,window)
        displayComponentHelper.changeStatusBarColor(R.color.white)

        // View Model instance
        viewModel = ViewModelProvider(this)[LoginActivityViewModel::class.java]

        // Initialize the layout with view id
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            lifecycleScope.launch {
                viewModel.login(email,password,listener)
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    // Listener when authentication login service is called
    private val listener = object: AuthService.AuthLoginInterface{
        override fun onCompleted(response: Response<AuthLoginResponse>) {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }
}