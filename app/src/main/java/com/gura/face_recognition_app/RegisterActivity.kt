package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.repository.AuthRepository
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.RegisterActivityViewModel
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var backButton: Button
    private lateinit var personalIdEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dobDateStringFormat: String
    private lateinit var factor: AppViewModelFactory
    private lateinit var viewModel: RegisterActivityViewModel

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        factor = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factor)[RegisterActivityViewModel::class.java]

        backButton = findViewById(R.id.backButton)
        registerButton = findViewById(R.id.registerButton)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        personalIdEditText = findViewById(R.id.personalIdEditText)

        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = fullNameEditText.text.split(" ")
            val firstname = name[0]
            val lastname = name[1]
            val personalId = personalIdEditText.text.toString()

            val data = RegisterRequest(email,password,firstname,lastname,personalId)

            lifecycleScope.launch {
                viewModel.register(data)
            }

        }

        viewModel.command.observe(this) {
            if(it.cmd == "AUTH_REGISTER_COMPLETED") {
                val intent = Intent(this@RegisterActivity,
                    JoinOrganizationActivity ::class.java
                )
                finishAffinity()
                startActivity(intent)
            }
        }
    }
}