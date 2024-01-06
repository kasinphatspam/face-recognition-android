package com.gura.face_recognition_app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chaos.view.PinView
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.services.AuthService
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.JoinOrganizationActivityViewModel
import kotlinx.coroutines.launch

class JoinOrganizationActivity : AppCompatActivity() {
    private lateinit var passcodePinView: PinView
    private lateinit var confirmButton: Button
    private lateinit var logoutButton: Button
    private lateinit var factory: AppViewModelFactory
    private lateinit var viewModel: JoinOrganizationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_organization)

        // Initialize helper for customizing display components
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        // Initialize ViewModel and Views
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[JoinOrganizationActivityViewModel::class.java]
        passcodePinView = findViewById(R.id.passcodePinView)
        confirmButton = findViewById(R.id.confirmButton)
        logoutButton = findViewById(R.id.backButton)
        confirmButton.visibility = View.INVISIBLE

        // Listen for changes in the pin input field
        passcodePinView.doOnTextChanged { text, start, before, count ->
            if (text!!.length == 6) {
                confirmButton.visibility = View.VISIBLE
                closeKeyboard()
            } else {
                confirmButton.visibility = View.INVISIBLE
            }
        }

        // Handle button click to commit the passcode
        confirmButton.setOnClickListener {
            commit(passcodePinView.text.toString())
        }

        logoutButton.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Observe ViewModel commands
        viewModel.cmd.observe(this) {
            when (it.cmd) {
                "JOIN_ORGANIZATION_COMPLETED" -> {
                    // Redirect to the main activity on success
                    val intent = Intent(this@JoinOrganizationActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                "ALREADY_REQUEST_TO_THIS_ORGANIZATION" -> {
                    Toast.makeText(
                        this,
                        "Already request to join this organization.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                "REQUEST_JOIN_SUCCESS" -> {
                    Toast.makeText(
                        this,
                        "The request has been sent. Please wait for the admin to accept.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                "JOIN_ORGANIZATION_FAILURE" -> {
                    Toast.makeText(
                        this,
                        "Passcode you entered incorrect.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Function to close the keyboard
    private fun closeKeyboard() {
        val view = currentFocus
        if (view != null) {
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // Function to commit the passcode using a coroutine
    private fun commit(passcode: String) {
        lifecycleScope.launch {
            viewModel.commit(passcode)
        }
    }
}
