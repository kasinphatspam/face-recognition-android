package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.AuthCommand
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.repository.AuthRepository
import com.gura.face_recognition_app.services.AuthService
import retrofit2.Response

class RegisterActivityViewModel(val application: Application): ViewModel() {

    private val authService = AuthService(application)
    val command = MutableLiveData<AuthCommand>()

    suspend fun register(email: String,
                         password: String,
                         firstname: String,
                         lastname: String,
                         personalId: String)
    {
        authService.register(email, password, firstname, lastname, personalId) {
            success ->
            if (success) {
                command.apply {
                    value = AuthCommand("AUTH_REGISTER_COMPLETED")
                }
            }
            command.apply {
                value = AuthCommand("AUTH_REGISTER_FAILURE")
            }
        }
    }
}