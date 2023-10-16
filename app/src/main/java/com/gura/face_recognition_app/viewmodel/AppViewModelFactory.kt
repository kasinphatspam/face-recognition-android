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
        } else if (modelClass.isAssignableFrom(RealtimeCameraActivityViewModel::class.java)) {
            return RealtimeCameraActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(ShareFragmentViewModel::class.java)) {
            return ShareFragmentViewModel(application) as T
        } else if (modelClass.isAssignableFrom(AddContactActivityViewModel::class.java)) {
            return AddContactActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(RegisterActivityViewModel::class.java)) {
            return RegisterActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(EncodeContactActivityViewModel::class.java)) {
            return EncodeContactActivityViewModel(application) as T
        } else if (modelClass.isAssignableFrom(JoinOrganizationActivityViewModel::class.java)) {
            return JoinOrganizationActivityViewModel(application) as T
        }
        throw IllegalArgumentException("UnknownViewModel")
    }
}