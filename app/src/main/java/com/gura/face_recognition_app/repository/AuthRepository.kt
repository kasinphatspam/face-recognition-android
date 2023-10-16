package com.gura.face_recognition_app.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.common.internal.service.Common
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.data.request.LoginRequest
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.data.response.LoginResponse
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import org.json.JSONObject
import retrofit2.Response


class AuthRepository(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    interface AuthLoginInterface {
        fun onResponse(response: Response<LoginResponse>)
        fun onFailure(error: String)
    }

    interface AuthRegisterInterface {
        fun onResponse(response: Response<RegisterResponse>)
        fun onFailure(error: String)
    }

    fun currentUser(): Int {
        val preferences = SharePreferencesHelper(context).getInstance()
        if (preferences.getInt("userId", -1) != -1) {
            return preferences.getInt("userId", -1)
        }
        return -1
    }

    fun updateUserId(userId: Int) {
        // Set userId into Global variable on Application Class
        val app = App.instance
        app.userId = userId
        // Saving userId into Share Preferences
        val preferences = SharePreferencesHelper(context).getInstance()
        preferences.edit().putInt("userId", userId).apply()
    }

    suspend fun login(data: LoginRequest, listener: AuthLoginInterface) {
        val response = api.login(data)

        if (response!!.isSuccessful) {
            updateUserId(response.body()!!.id)
            Log.e("Auth", "Set user id ${response.body()}")
            listener.onResponse(response)
            return
        }
        val errorBody = response.errorBody()!!.string()
        listener.onFailure(errorBody)
        Log.e("AuthRepository", errorBody)
    }

    suspend fun register(data: RegisterRequest, listener: AuthRegisterInterface) {
        val response = api.register(data)

        if (response!!.isSuccessful) {
            listener.onResponse(response)
            return
        }
        val errorBody = response.errorBody()!!.string()
        listener.onFailure(errorBody)
        Log.e("AuthRepository", errorBody)
    }
}