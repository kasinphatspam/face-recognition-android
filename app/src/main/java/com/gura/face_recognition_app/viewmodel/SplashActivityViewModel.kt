package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.helper.NetworkHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.repository.AuthRepository
import com.gura.face_recognition_app.repository.ConnectionRepository


class SplashActivityViewModel(private val application: Application) :
    ViewModel() {

    var isConnected = MutableLiveData<Boolean>(false)
    private val connectionRepository = ConnectionRepository(application)
    private val authRepository = AuthRepository(application)
    private val preferencesHelper = SharePreferencesHelper(application)
    private val networkHelper = NetworkHelper(application)

    /* --------------------- Network Function -----------------------*/
    fun isNetworkConnected(): Boolean {
        return networkHelper.isNetworkConnected()
    }

    /* --------------------- Connection Function -----------------------*/
    fun checkServerConnection() {
        var connection1 = false
        var connection2 =false

        connectionRepository.checkBackendServerStatus(
            object : ConnectionRepository.CheckServerStatusInterface{
            override fun onConnected() {
                Log.d("Connection", "Server connection1 is connected")
                connection1 = true
                if(connection2){
                    isConnected.apply {
                        value = true
                    }
                }
            }
            override fun onDisconnected(errorMessage: String) {
                connection1 = false
            }
        })
        connectionRepository.checkMlServerStatus(
            object : ConnectionRepository.CheckServerStatusInterface{
            override fun onConnected() {
                Log.d("Connection", "Server connection2 is connected")
                connection2 = true
                if(connection1){
                    isConnected.apply {
                        value = true
                    }
                }
            }
            override fun onDisconnected(errorMessage: String) {
                connection2 = false
            }
        })
    }

    /* --------------------- Authentication Function -----------------------*/
    fun getCurrentSignedInAccount(): Int {
        return authRepository.currentUser()
    }

    fun setUserId(): Boolean{
        val userId = preferencesHelper.getInstance().getInt("userId",-1)
        if(userId != -1){
            authRepository.updateUserId(userId)
            return true
        }
        return false
    }
}