package com.gura.face_recognition_app.repository

import android.content.Context
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.model.AuthLoginRequest
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.model.AuthRegisterRequest
import com.gura.face_recognition_app.model.AuthRegisterResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthRepository(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    interface AuthLoginInterface {
        fun onCompleted(response: Response<AuthLoginResponse>)
        fun onFailed()
    }

    interface AuthRegisterInterface {
        fun onCompleted(response: Response<AuthRegisterResponse>)
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

    suspend fun login(email: String, password: String, listener: AuthLoginInterface) {
        val authLoginRequest = AuthLoginRequest(email, password)
        val response = api.login(authLoginRequest)

        if (response!!.isSuccessful) {
            updateUserId(response.body()!!.userId)
            listener.onCompleted(response)
            return
        }
        listener.onFailed()
    }

    suspend fun register(
        email: String, password: String, firstname: String, lastname: String,
        personalId: String, listener: AuthRegisterInterface
    ) {
        val authRegisterRequest = AuthRegisterRequest(
            email, password, firstname, lastname, personalId
        )
        val response = api.register(authRegisterRequest)
        if (response!!.isSuccessful) {
            listener.onCompleted(response)
        }
    }
}