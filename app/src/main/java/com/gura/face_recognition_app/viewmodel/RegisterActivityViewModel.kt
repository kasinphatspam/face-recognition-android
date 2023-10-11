package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.AuthCommand
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.repository.AuthRepository
import retrofit2.Response

class RegisterActivityViewModel(val application: Application): ViewModel() {

    private val authRepository = AuthRepository(application)
    val command = MutableLiveData<AuthCommand>()

    suspend fun register(data: RegisterRequest){
        authRepository.register(data, listener)
    }

    private val listener = object: AuthRepository.AuthRegisterInterface {
        override fun onResponse(response: Response<RegisterResponse>) {
            authRepository.updateUserId(response.body()!!.user.id)
            command.apply {
                value = AuthCommand("AUTH_REGISTER_COMPLETED")
            }
        }

        override fun onFailure(error: String) {
            TODO("Not yet implemented")
        }
    }
}