package com.gura.face_recognition_app.repository

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.helper.RetrofitHelper
import retrofit2.Response

class UserRepository(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    interface GetUserInterface {
        fun onResponse(response: Response<User>)
        fun onFailure(error: String)
    }

    interface UpdateUserInterface {
        fun onResponse(message: String)
        fun onFailure(error: String)
    }

    interface DeleteUserInterface {
        fun onResponse(message: String)
        fun onFailure(error: String)
    }

    // Get current user
    suspend fun loadCurrentUserAsync(listener: GetUserInterface) {
        val authRepository = AuthRepository(context)
        val response = api.getUserById(authRepository.currentUser())

        if (response.isSuccessful) {
            listener.onResponse(response)
            return
        }
        listener.onFailure(response.raw().message)
    }

    // Get user data by id
    suspend fun loadUserById(id: Int, listener: GetUserInterface) {
        val response = api.getUserById(id)

        if (response.isSuccessful) {
            listener.onResponse(response)
            return
        }
        listener.onFailure(response.raw().message)
    }

    // Update user data by id
    fun updateUserById(id: Int, data: User, listener: UpdateUserInterface) {
        val response = api.update(id, data)

        if (response.isSuccessful) {
            listener.onResponse(response.body()!!.message)
            return
        }
        listener.onFailure(response.raw().message)
    }

}