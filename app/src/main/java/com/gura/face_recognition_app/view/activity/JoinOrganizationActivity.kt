package com.gura.face_recognition_app.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chaos.view.PinView
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.JoinOrganizationActivityViewModel
import kotlinx.coroutines.launch


class JoinOrganizationActivity : AppCompatActivity() {

    private lateinit var passcodePinView: PinView
    private lateinit var confirmButton: Button
    private lateinit var factory: AppViewModelFactory
    private lateinit var viewModel: JoinOrganizationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_organization)

        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[JoinOrganizationActivityViewModel::class.java]

        passcodePinView = findViewById(R.id.passcodePinView)
        confirmButton = findViewById(R.id.confirmButton)
        confirmButton.visibility = View.INVISIBLE

        passcodePinView.doOnTextChanged { text, start, before, count ->
            if(text!!.length == 6){
                confirmButton.visibility = View.VISIBLE
                closeKeyboard()
            }else{
                confirmButton.visibility = View.INVISIBLE
            }
        }

        confirmButton.setOnClickListener {
            commit(passcodePinView.text.toString())
        }

        viewModel.cmd.observe(this) {
            if (it.cmd == "JOIN_ORGANIZATION_SUCCESS"){
                val intent = Intent(this@JoinOrganizationActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        val view = this.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }

    private fun commit(passcode: String){
        lifecycleScope.launch {
            viewModel.commit(passcode)
        }
    }
}