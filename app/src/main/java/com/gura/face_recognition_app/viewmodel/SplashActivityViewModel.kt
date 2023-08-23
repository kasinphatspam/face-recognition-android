package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.gura.face_recognition_app.service.AuthService
import com.gura.face_recognition_app.service.ConnectionService


class SplashActivityViewModel(private val application: Application) :
    AndroidViewModel(application) {

    var isConnectionToBackend: Boolean = false
    var isConnectionToMlServer: Boolean = false

    private val classTag = "SplashActivityViewModel"
    private val backendConnectionTag = "BACKEND_CONNECTION"
    private val mlConnectionTag = "ML_CONNECTION"
    private val connectionService = ConnectionService(application)
    private val authService = AuthService(application)

    /* --------------------- Connection Function -----------------------*/
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val capabilities =
            connectivityManager!!.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
        return false
    }

    fun checkServerConnection(): Boolean {
        connectionService.checkBackendServerStatus(backendConnectionTag, connectionListener)
        connectionService.checkMlServerStatus(mlConnectionTag, connectionListener)
        return true
    }

    private val connectionListener = object : ConnectionService.CheckServerStatusInterface {
        override fun onConnected(tag: String) {
            Log.d(classTag, "$tag is already connected")
            when (tag) {
                backendConnectionTag -> {
                    isConnectionToBackend = true
                }

                mlConnectionTag -> {
                    isConnectionToMlServer = true
                }
            }
        }

        override fun onDisconnected(tag: String, errorMessage: String) {
            when (tag) {
                backendConnectionTag -> {
                    isConnectionToBackend = false
                }

                mlConnectionTag -> {
                    isConnectionToMlServer = false
                }
            }
        }
    }

    /* --------------------- Authentication Function -----------------------*/
    fun getCurrentSignedInAccount(): Int {
        return authService.currentUser()
    }
}