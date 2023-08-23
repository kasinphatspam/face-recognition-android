package com.gura.face_recognition_app.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.gura.face_recognition_app.MainActivity
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.viewmodel.SplashActivityViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val mInterval: Long = 2000
    private var handler: Handler? = null
    private lateinit var viewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize helper for customizing display component
        val displayComponentHelper = DisplayComponentHelper(this@SplashActivity, window)
        displayComponentHelper.changeStatusBarColor(R.color.white)

        // View Model instance
        viewModel = ViewModelProvider(this)[SplashActivityViewModel::class.java]
        handler = Handler(Looper.getMainLooper())
        startRepeatingTask()
    }

    // Create loop for checking preparation of connection
    private var statusChecker: Runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            try {
                viewModel.checkServerConnection()
                // Change the intent activity when the server is online.
                if (viewModel.isConnectionToBackend &&
                    viewModel.isConnectionToMlServer &&
                    viewModel.isNetworkConnected())
                {
                    stopRepeatingTask()
                    // Read file and check if userId in Share Preferences is not null
                    if (viewModel.getCurrentSignedInAccount() != -1) {
                        // Move to main activity and close all previous activity
                        val intent = Intent(
                            this@SplashActivity, MainActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(
                            this@SplashActivity, LoginActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }

            } finally {
                // Add time delay
                handler!!.postDelayed(this, mInterval)
            }
        }
    }

    private fun startRepeatingTask() {
        statusChecker.run()
    }

    private fun stopRepeatingTask() {
        handler!!.removeCallbacks(statusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }

}