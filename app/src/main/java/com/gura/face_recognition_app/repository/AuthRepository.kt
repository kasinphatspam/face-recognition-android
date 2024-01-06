package com.gura.face_recognition_app.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.common.internal.service.Common
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.data.model.User
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
        fun onResponse(response: LoginResponse)
        fun onFailure(error: String)
    }

    interface AuthRegisterInterface {
        fun onSuccess(response: RegisterResponse)
        fun onFailure(error: String)
    }

    interface AuthMeInterface {
        fun onSuccess(user: User)
        fun onFailure()
    }

    suspend fun currentUser(session: String, listener: AuthMeInterface) {
        val headers = HashMap<String, String>()
        headers["session"] = session
        val response = api.me(headers)

        if (response.isSuccessful) {
            listener.onSuccess(response.body()!!)
            return
        }
        listener.onFailure()
    }

    suspend fun login(data: LoginRequest, listener: AuthLoginInterface) {
        val response = api.login(data)

        if (response!!.isSuccessful) {
            listener.onResponse(response.body()!!)
            return
        }
        val errorBody = response.errorBody()!!.string()
        listener.onFailure(errorBody)
        Log.e("AuthRepository", errorBody)
    }

    suspend fun register(data: RegisterRequest, listener: AuthRegisterInterface) {
        val response = api.register(data)

        if (response!!.isSuccessful) {
            listener.onSuccess(response.body()!!)
            return
        }
        val errorBody = response.errorBody()!!.string()
        listener.onFailure(errorBody)
        Log.e("AuthRepository", errorBody)
    }
}