package com.gura.face_recognition_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.chaos.view.PinView
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.view.MainActivity
import kotlinx.coroutines.launch


class JoinOrganizationActivity : AppCompatActivity() {

    private lateinit var passcodePinView: PinView
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_organization)

        val displayComponentHelper = DisplayComponentHelper(this@JoinOrganizationActivity,window)
        displayComponentHelper.changeStatusBarColor(R.color.white)

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
        val organizationRepository = OrganizationRepository(this)
        val app = App.instance
        lifecycleScope.launch {
            organizationRepository.join(app.userId!!, passcode, object: OrganizationRepository.JoinOrganizationInterface{
                override fun onCompleted() {
                    val intent = Intent(this@JoinOrganizationActivity, MainActivity::class.java)
                    startActivity(intent)
                }

            })
        }
    }
}