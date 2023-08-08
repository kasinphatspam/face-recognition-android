package com.gura.face_recognition_app.service

import android.content.Context
import com.gura.face_recognition_app.api.UserAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.model.UserInformationRequest
import com.gura.face_recognition_app.model.UserInformationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class UserService (val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(UserAPI::class.java)

    interface UserInformationInterface {
        fun onCompleted(response: Response<UserInformationResponse>)
    }

    suspend fun getCurrentUser(listener: UserInformationInterface){
        val authService = AuthService(context)
        val response = api.getUserById(authService.currentUser())

        if(response.isSuccessful){
            listener.onCompleted(response)
        }
    }

}