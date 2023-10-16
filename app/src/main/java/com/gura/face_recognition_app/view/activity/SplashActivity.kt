package com.gura.face_recognition_app.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.SplashActivityViewModel
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val INTERVAL = 2000L
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

        viewModel.command.observe(this) { command ->
            when (command.cmd) {
                "NOT_FOUND_ORGANIZATION" -> navigateToActivity(JoinOrganizationActivity::class.java)
                else -> navigateToActivity(MainActivity::class.java)
            }
        }

        viewModel.isConnected.observe(this) { isConnected ->
            if (isConnected) {
                stopRepeatingTask()
                if (viewModel.currentUser() != -1) {
                    viewModel.setUserId()
                    lifecycleScope.launch {
                        viewModel.hasJoinedOrganization()
                    }
                } else {
                    navigateToActivity(LoginActivity::class.java)
                }
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
                viewModel.checkServerStatus()
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
