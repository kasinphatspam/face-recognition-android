package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gura.face_recognition_app.service.RecognitionService

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        val handler = Handler()
        handler.postDelayed({
            val recognitionService = RecognitionService(this@SplashActivity)

            recognitionService.checkServerStatus(object: RecognitionService.ServerInterface{
                override fun onConnected() {
                    val intent = Intent(this@SplashActivity,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }

                override fun onDisconnected(msg: String) {
                    Log.d("SplashActivity",msg)
                }

            })
        }, 2000)
    }
}