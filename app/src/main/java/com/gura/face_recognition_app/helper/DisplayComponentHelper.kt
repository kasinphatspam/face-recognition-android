package com.gura.face_recognition_app.helper

import android.content.Context
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import com.gura.face_recognition_app.R

class DisplayComponentHelper(
    private val context: Context,
    private val window: Window) {

    fun changeStatusBarColor(color: Int){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(context, color)
    }

    fun changeNavigationBarColor(color: Int){
        window.navigationBarColor = ContextCompat.getColor(context, color)
    }
}