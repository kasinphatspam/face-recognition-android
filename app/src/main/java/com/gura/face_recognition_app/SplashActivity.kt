package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.gura.face_recognition_app.service.AuthService
import com.gura.face_recognition_app.service.RecognitionService
import com.gura.face_recognition_app.helper.SharePreferencesHelper

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val mInterval: Long = 2000
    private var handler: Handler? = null
    var isServerOnConnected: Boolean = false
    var isRecognitionServerConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        handler = Handler()
        startRepeatingTask()
    }

    private var statusChecker: Runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            try {
                // Check server status
                val recognitionService = RecognitionService(this@SplashActivity)
                recognitionService.checkServerStatus(
                    object: RecognitionService.CheckServerStatusInterface{
                    override fun onConnected() {
                        isRecognitionServerConnected = true
                    }

                    override fun onDisconnected(msg: String) {
                        Log.d("RecognitionServerAPI",msg)
                        isServerOnConnected = false
                    }

                })

                val authService = AuthService(this@SplashActivity)
                authService.checkServerStatus(
                    object : AuthService.CheckServerStatusInterface{
                    override fun onConnected() {
                        isServerOnConnected = true
                    }

                    override fun onDisconnected(msg: String) {
                        Log.d("ServerAPI",msg)
                        isServerOnConnected = false
                    }
                })

                // Change the intent activity when the server is online.
                if(isRecognitionServerConnected && isServerOnConnected){
                    stopRepeatingTask()

                    // Read file and check if userId in Share Preferences is not null
                    if(authService.currentUser() != -1){
                        // Get userId from Share Preferences (-1: null)
                        val userId: Int = authService.currentUser()
                        authService.save(userId)
                        // Move to main activity and close all previous activity
                        val intent = Intent(this@SplashActivity,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }

            } finally {
                handler!!.postDelayed(this, mInterval)
            }
        }
    }

    private fun startRepeatingTask(){
        statusChecker.run()
    }

    private fun stopRepeatingTask(){
        handler!!.removeCallbacks(statusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }

}