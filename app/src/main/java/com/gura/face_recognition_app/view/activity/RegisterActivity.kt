package com.gura.face_recognition_app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.RegisterActivityViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var registerButton: Button
    private lateinit var backButton: Button
    private lateinit var personalIdEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var passwordEditText: EditText

    // Declare ViewModel and ViewModelFactory
    private lateinit var factor: AppViewModelFactory
    private lateinit var viewModel: RegisterActivityViewModel

    // Suppress some lint warnings
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize helper for customizing display components
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        // Initialize ViewModel and ViewModelFactory
        factor = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factor)[RegisterActivityViewModel::class.java]

        // Initialize UI elements with view IDs
        backButton = findViewById(R.id.backButton)
        registerButton = findViewById(R.id.registerButton)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        personalIdEditText = findViewById(R.id.personalIdEditText)

        // Handle back button click
        backButton.setOnClickListener {
            finish()
        }

        // Handle register button click
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val name = fullNameEditText.text.split(" ")
            val firstname = name[0]
            val lastname = name[1]
            val personalId = personalIdEditText.text.toString()

            // Create a data object for registration
            val data = RegisterRequest(email, password, firstname, lastname, personalId)

            // Use a coroutine for registration
            lifecycleScope.launch {
                viewModel.register(data)
            }
        }

        // Observe registration command from the ViewModel
        viewModel.command.observe(this) { command ->
            if (command.cmd == "AUTH_REGISTER_COMPLETED") {
                // If registration is successful, navigate to the JoinOrganizationActivity
                val intent = Intent(this@RegisterActivity, JoinOrganizationActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
    }
}
