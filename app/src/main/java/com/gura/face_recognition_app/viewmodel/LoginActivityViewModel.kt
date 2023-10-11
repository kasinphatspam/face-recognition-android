package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.AuthCommand
import com.gura.face_recognition_app.data.request.LoginRequest
import com.gura.face_recognition_app.data.response.LoginResponse
import com.gura.face_recognition_app.repository.AuthRepository
import retrofit2.Response

class LoginActivityViewModel(application: Application) :
    ViewModel() {

    private val authRepository = AuthRepository(application)
    var authCmd = MutableLiveData<AuthCommand>()

    suspend fun login (email:String, password: String) {
        val data = LoginRequest(email, password)
        authRepository.login(data, object: AuthRepository.AuthLoginInterface{
            override fun onResponse(response: Response<LoginResponse>) {
                setUserId(response.body()!!.userId)
                authCmd.apply {
                    value = AuthCommand("AUTH_LOGIN_COMPLETED")
                }
            }
            override fun onFailure() {
                authCmd.apply {
                    value = AuthCommand("AUTH_LOGIN_FAILED")
                }
            }
        })
    }

    fun setUserId(userId: Int){
        authRepository.updateUserId(userId)
    }
}