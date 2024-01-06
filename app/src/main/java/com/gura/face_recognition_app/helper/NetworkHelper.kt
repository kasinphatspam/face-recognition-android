package com.gura.face_recognition_app.helper

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class NetworkHelper(val application: Application) {

    fun isNetworkConnected(): Boolean {
//        val connectivityManager =
//            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        val capabilities =
//            connectivityManager!!.getNetworkCapabilities(connectivityManager.activeNetwork)
//
//        if (capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
//            return true
//        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
//            return true
//        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
//            return true
//        }
//        return false
        return true;
    }
}