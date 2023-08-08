package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gura.face_recognition_app.model.AuthRegisterResponse
import com.gura.face_recognition_app.service.AuthService
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var backButton: Button
    private lateinit var genderSpinner: Spinner
    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var personalIdEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var dobDateStringFormat: String

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_purple)

        genderSpinner = findViewById(R.id.spinner)
        backButton = findViewById(R.id.backButton)
        dateTextView = findViewById(R.id.dateTextView)
        registerButton = findViewById(R.id.registerButton)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        firstnameEditText = findViewById(R.id.firstnameEditText)
        lastnameEditText = findViewById(R.id.lastnameEditText)
        personalIdEditText = findViewById(R.id.personalIdEditText)

        backButton.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val firstname = firstnameEditText.text.toString()
            val lastname = lastnameEditText.text.toString()
            val personalId = personalIdEditText.text.toString()
            val gender = genderSpinner.selectedItem.toString()

            val authService = AuthService(this)
            authService.register(
                email,password,firstname,lastname,gender,personalId,dobDateStringFormat,"default",
                object: AuthService.AuthRegisterInterface {
                override fun onCompleted(response: Response<AuthRegisterResponse>) {
                    authService.save(response.body()!!.userId)
                    val intent = Intent(this@RegisterActivity
                        ,MainActivity ::class.java)
                    finishAffinity()
                    startActivity(intent)
                }

            })

        }

        dateTextView.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            var dateConverted = ""
            var monthConverted = ""

            val datePickerDialog = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth ->
                    dateTextView.text =
                        (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    monthConverted = monthOfYear.toString()
                    dateConverted = dayOfMonth.toString()

                    if(monthOfYear+1 < 10){
                        monthConverted = "0${monthOfYear+1}"
                    }
                    if(dayOfMonth < 10){
                        dateConverted = "0$dayOfMonth"
                    }
                    dobDateStringFormat = "$year-${monthConverted}-${dateConverted}T00:00:00Z"
                }, year, month, day
            )
            datePickerDialog.show()
        }

        val gender = listOf("Male","Female","Others")
        val adapter = ArrayAdapter(this,R.layout.spinner_item,gender)
        adapter.setDropDownViewResource(R.layout.spinner_item)

        genderSpinner.adapter = adapter

    }
}