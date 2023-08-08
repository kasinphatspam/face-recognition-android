package com.gura.face_recognition_app.helper

import android.content.Context
import android.content.SharedPreferences
import com.gura.face_recognition_app.R

class SharePreferencesHelper(val context: Context) {

    private val name = context.getString(R.string.preference_file_key)
    private val mode = Context.MODE_PRIVATE
    private val preferences = context.getSharedPreferences(name, mode)

    fun getInstance(): SharedPreferences {
        return preferences
    }

}