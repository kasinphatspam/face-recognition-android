package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.helper.NetworkHelper
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.repository.AuthRepository
import com.gura.face_recognition_app.repository.ConnectionRepository
import com.gura.face_recognition_app.repository.OrganizationRepository

class SplashActivityViewModel(application: Application) : ViewModel() {

    // Constants
    companion object {
        private const val KEY_USER_ID: String = "userId"
    }

    // LiveData
    var isConnected = MutableLiveData(false)
    val command = MutableLiveData<Command>()

    // Repositories and Helpers
    private val networkHelper = NetworkHelper(application)
    private val preferencesHelper = SharePreferencesHelper(application)
    private val connectionRepository = ConnectionRepository(application)
    private val authRepository = AuthRepository(application)
    private val organizationRepository = OrganizationRepository(application)

    // Network Status
    fun isNetworkConnected(): Boolean {
        return networkHelper.isNetworkConnected()
    }

    fun checkServerStatus() {
        connectionRepository.checkBackendServerStatus(connectionListener)
    }

    // User Information
    fun currentUser(): Int {
        return authRepository.currentUser()
    }

    suspend fun hasJoinedOrganization() {
        organizationRepository.getCurrentOrganization(organizationListener)
    }

    fun setUserId() {
        val userId = preferencesHelper.getInstance().getInt(KEY_USER_ID, -1)
        if (userId != -1) {
            authRepository.updateUserId(userId)
        }
    }

    // Listeners
    private val connectionListener = object : ConnectionRepository.CheckServerStatusInterface {
        override fun onConnected() {
            isConnected.value = true
        }

        override fun onDisconnected(errorMessage: String) {
            isConnected.value = false
        }
    }

    private val organizationListener = object : OrganizationRepository.OrganizationInformationInterface {
        override fun onResponse(data: Organization) {
            command.value = Command("FOUND_ORGANIZATION")
        }

        override fun onFailure(error: String) {
            command.value = Command("NOT_FOUND_ORGANIZATION")
        }
    }
}
