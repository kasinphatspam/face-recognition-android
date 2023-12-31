package com.gura.face_recognition_app.repository

import android.content.Context
import android.util.Log
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.helper.MLRetrofitHelper
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.data.response.ServerStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConnectionRepository(val context: Context) {

    interface CheckServerStatusInterface {
        fun onConnected()
        fun onDisconnected(errorMessage: String)
    }

    private val backendAPI = RetrofitHelper
        .getInstance(context)
        .create(BackendAPI::class.java)

    private val mlAPI = MLRetrofitHelper
        .getInstance(context)
        .create(BackendAPI::class.java)

    fun checkBackendServerStatus(listener: CheckServerStatusInterface){
        val response = backendAPI.checkServerStatus()
        response.enqueue(object: Callback<ServerStatus> {
            override fun onResponse(call: Call<ServerStatus>, response: Response<ServerStatus>) {
                Log.e("SplashActivity", response.body().toString())
                if(response.isSuccessful){
                    listener.onConnected()
                }else{
                    // Handler error
                    val message = response.errorBody().toString()
                    listener.onDisconnected(message)
                }
            }
            override fun onFailure(call: Call<ServerStatus>, t: Throwable) {
                val message = t.message.toString()
                listener.onDisconnected(message)
            }

        })
    }

    fun checkMlServerStatus(listener: CheckServerStatusInterface){
        val response = mlAPI.checkServerStatus()
        response.enqueue(object: Callback<ServerStatus> {
            override fun onResponse(call: Call<ServerStatus>, response: Response<ServerStatus>) {
                if(response.isSuccessful){
                    listener.onConnected()
                }else{
                    // Handler error
                    val message = response.errorBody().toString()
                    listener.onDisconnected(message)
                }
            }
            override fun onFailure(call: Call<ServerStatus>, t: Throwable) {
                val message = t.message.toString()
                listener.onDisconnected(message)
            }
        })
    }
}