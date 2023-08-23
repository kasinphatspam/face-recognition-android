package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gura.face_recognition_app.service.AuthService

class LoginActivityViewModel(application: Application) :
    AndroidViewModel(application) {

    private val serverService = AuthService(application)

    suspend fun login (email:String, password: String, listener: AuthService.AuthLoginInterface){
        serverService.login(email,password,listener)
    }
}