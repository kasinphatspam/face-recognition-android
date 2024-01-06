package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.helper.NetworkHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.repository.ConnectionRepository
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.services.AuthService

class SplashActivityViewModel(application: Application) : ViewModel() {

    // LiveData
    var isConnected = MutableLiveData(false)
    val command = MutableLiveData<Command>()
    var user = MutableLiveData<User>()

    // Repositories and Helpers
    private val networkHelper = NetworkHelper(application)
    private val authService = AuthService(application)

    // Network Status
    fun isNetworkConnected(): Boolean {
        return networkHelper.isNetworkConnected()
    }

    // User Information
    suspend fun currentUser() {
        Log.d("SplashActivityViewModel", "Get current user")
        authService.getCurrentUser() { user ->
            if (user != null) {
                Log.d("SplashActivityViewModel", user.email)
                this.user.value = user
                command.apply {
                    value = Command("ALREADY_LOGIN")
                }
            }else {
                Log.d("SplashActivityViewModel", "null")
                command.apply {
                    value = Command("NOT_LOGIN")
                }
            }
        }
    }
}
