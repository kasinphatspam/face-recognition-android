package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.AuthCommand
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.repository.AuthRepository
import retrofit2.Response

class LoginActivityViewModel(application: Application) :
    ViewModel() {

    private val authRepository = AuthRepository(application)
    var authCmd = MutableLiveData<AuthCommand>()

    suspend fun login (email:String, password: String){
        authRepository.login(email,password,
            object: AuthRepository.AuthLoginInterface{
                override fun onCompleted(response: Response<AuthLoginResponse>) {
                    setUserId(response.body()!!.userId)
                    authCmd.apply {
                        value = AuthCommand("AUTH_LOGIN_COMPLETED")
                    }
                }
                override fun onFailed() {
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