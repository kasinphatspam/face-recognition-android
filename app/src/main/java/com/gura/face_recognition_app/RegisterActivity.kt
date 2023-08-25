package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.model.AuthRegisterResponse
import com.gura.face_recognition_app.repository.AuthRepository
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

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

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

            val authRepository = AuthRepository(this)

            lifecycleScope.launch {

                authRepository.register(
                    email,password,firstname,lastname,personalId,
                    object: AuthRepository.AuthRegisterInterface {
                        override fun onCompleted(response: Response<AuthRegisterResponse>) {
                            authRepository.updateUserId(response.body()!!.userId)
                            val intent = Intent(this@RegisterActivity
                                ,JoinOrganizationActivity ::class.java)
                            finishAffinity()
                            startActivity(intent)
                        }

                    })
            }

        }
    }
}