package com.gura.face_recognition_app.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.MainActivity
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.api.AuthAPI
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.model.AuthLoginRequest
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.model.AuthRegisterRequest
import com.gura.face_recognition_app.model.AuthRegisterResponse
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.model.ServerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class AuthService(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(AuthAPI::class.java)

    interface AuthLoginInterface {
        fun onCompleted(response: Response<AuthLoginResponse>)
    }

    interface AuthRegisterInterface {
        fun onCompleted(response: Response<AuthRegisterResponse>)
    }

    interface CheckServerStatusInterface {
        fun onConnected()
        fun onDisconnected(msg: String)
    }

    fun currentUser(): Int {
        val preferences = SharePreferencesHelper(context).getInstance()
        if(preferences.getInt("userId",-1) != -1){
            return preferences.getInt("userId",-1)
        }
        return -1
    }

    fun save(userId: Int){
        // Set userId into Global variable on Application Class
        val app = App.instance
        app.userId = userId
        // Saving userId into Share Preferences
        val preferences = SharePreferencesHelper(context).getInstance()
        preferences.edit().putInt("userId", userId).apply()
    }

    fun login(email: String, password: String, listener: AuthLoginInterface){

        CoroutineScope(Dispatchers.IO).launch {
            val authLoginRequest = AuthLoginRequest(email,password)
            val response = api.login(authLoginRequest)

            if(response!!.isSuccessful){
                save(response.body()!!.userId)
                listener.onCompleted(response)
            }
        }

    }

    fun register(
        email: String,
        password: String,
        firstname: String,
        lastname: String,
        gender: String,
        personalId: String,
        dob: String,
        profileImage: String,
        listener: AuthRegisterInterface
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val authRegisterRequest = AuthRegisterRequest(
                email, password, firstname, lastname, gender, personalId, dob, profileImage
            )
            val response = api.register(authRegisterRequest)
            if(response!!.isSuccessful){
                listener.onCompleted(response)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkServerStatus(listener: CheckServerStatusInterface){
        GlobalScope.launch {
            val status = api.checkServerStatus()
            status.enqueue(object: Callback<ServerStatus> {
                override fun onResponse(call: Call<ServerStatus>, response: Response<ServerStatus>) {
                    if(response.isSuccessful){
                        Log.d("API",response.body().toString())
                        listener.onConnected()
                    }
                }

                override fun onFailure(call: Call<ServerStatus>, t: Throwable) {
                    Log.e("API",t.message.toString())
                    listener.onDisconnected(t.message.toString())
                }

            })
        }
    }
}