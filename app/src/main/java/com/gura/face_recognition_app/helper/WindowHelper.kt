package com.gura.face_recognition_app.helper

import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class WindowHelper(private val context: Context, private val window: Window) {

    var allowNightMode: Boolean? = null
    var keepScreenOn: Boolean? = null
    var statusBarColor: Int? = null
    var navigationBarColor: Int? = null

    fun publish() {
        // Change status bar color
        if (statusBarColor != null) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(context, statusBarColor!!)
        }

        // Change navigation bar color
        if (navigationBarColor != null) {
            window.navigationBarColor = ContextCompat.getColor(context, navigationBarColor!!)
        }

        // Allow device's night mode
        if (allowNightMode != null && allowNightMode != false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Allow activity for keeping screen on
        if (keepScreenOn != null && keepScreenOn != false) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}