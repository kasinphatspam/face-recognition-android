package com.gura.face_recognition_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.view.LoginActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val displayComponentHelper = DisplayComponentHelper(this,window)
        displayComponentHelper.changeStatusBarColor(R.color.white)

        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val preferencesHelper = SharePreferencesHelper(this)
            preferencesHelper.getInstance().edit {
                clear()
            }
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}