package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(LoginActivityViewModel::class.java)) {
            return LoginActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(SplashActivityViewModel::class.java)) {
            return SplashActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(CameraActivityViewModel::class.java)) {
            return CameraActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(RealtimeCameraActivityViewModel::class.java))
            return RealtimeCameraActivityViewModel(application) as T
        throw IllegalArgumentException("UnknownViewModel")
    }
}