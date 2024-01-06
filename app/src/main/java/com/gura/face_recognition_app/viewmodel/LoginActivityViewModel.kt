package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.AuthCommand
import com.gura.face_recognition_app.data.request.LoginRequest
import com.gura.face_recognition_app.data.response.LoginResponse
import com.gura.face_recognition_app.repository.AuthRepository
import com.gura.face_recognition_app.services.AuthService
import retrofit2.Response

class LoginActivityViewModel(application: Application) :
    ViewModel() {

    private val authService = AuthService(application)
    var authCmd = MutableLiveData<AuthCommand>()

    suspend fun login(email: String, password: String) {
        authService.login(email, password) { success ->
            if (success) {
                authCmd.apply {
                    value = AuthCommand("AUTH_LOGIN_COMPLETED")
                }
            } else {
                authCmd.apply {
                    value = AuthCommand("AUTH_LOGIN_FAILED")
                }
            }
        }
    }
}