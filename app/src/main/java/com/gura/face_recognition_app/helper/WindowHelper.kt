package com.gura.face_recognition_app.helper

import android.content.Context
import android.util.Log
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
            Log.i("WindowHelper", "Set status bar color to #${statusBarColor}")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(context, statusBarColor!!)
        }

        // Change navigation bar color
        if (navigationBarColor != null) {
            Log.i("WindowHelper", "Set navigation bar color to #${navigationBarColor}")
            window.navigationBarColor = ContextCompat.getColor(context, navigationBarColor!!)
        }

        // Allow device's night mode
        if (allowNightMode != null && allowNightMode != false) {
            Log.i("WindowHelper", "Disable device's night mode")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Allow activity for keeping screen on
        if (keepScreenOn != null && keepScreenOn != false) {
            Log.i("WindowHelper", "Enable keep screen on")
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}