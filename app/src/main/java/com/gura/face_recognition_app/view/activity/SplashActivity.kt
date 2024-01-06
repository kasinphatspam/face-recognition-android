package com.gura.face_recognition_app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.services.AuthService
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.SplashActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val INTERVAL = 2000L
        private const val TAG = "SplashActivity"
    }

    private var handler: Handler? = null

    private lateinit var viewModel: SplashActivityViewModel
    private lateinit var factory: AppViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize helper for customizing display component
        WindowHelper(this, window).apply {
            statusBarColor = R.color.white
            allowNightMode = false
            publish()
        }

        // ViewModel instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[SplashActivityViewModel::class.java]
        handler = Handler(Looper.getMainLooper())
        startRepeatingTask()

        val authService = AuthService(this)
        Log.d(TAG,"SESSION ID: " +authService.getSessionId())

        viewModel.command.observe(this) {

            if (it.cmd == "ALREADY_LOGIN") {
                val user = viewModel.user.value!!
                Log.d(TAG, user.email)
                // set global variables
                App.instance.user = user
                if (it != null) {
                    if (user.organization != null) {
                        navigateToActivity(MainActivity::class.java)
                        return@observe
                    }
                    navigateToActivity(JoinOrganizationActivity::class.java)
                }
            } else if (it.cmd == "NOT_LOGIN") {
                navigateToActivity(LoginActivity::class.java)
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private val statusChecker: Runnable = object : Runnable {
        override fun run() {
            if (viewModel.isNetworkConnected()) {
                Log.d("SplashActivity", "--------------------------------------")
                lifecycleScope.launch {
                    viewModel.currentUser()
                }
            }
            handler?.postDelayed(this, INTERVAL)
        }
    }

    private fun startRepeatingTask() {
        statusChecker.run()
    }

    private fun stopRepeatingTask() {
        handler?.removeCallbacks(statusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }
}
