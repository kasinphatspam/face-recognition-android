package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.services.AuthService
import com.gura.face_recognition_app.services.OrganizationService

class JoinOrganizationActivityViewModel(private val application: Application)
    : ViewModel()
{
    private val authService = AuthService(application)
    private val organizationService = OrganizationService(application)
    var cmd = MutableLiveData<Command>()

    suspend fun commit(passcode: String) {
        organizationService.requestJoinOrgWithPasscode(passcode) {
            status ->

            when (status) {
                -1 -> cmd.value = Command("JOIN_ORGANIZATION_FAILURE")
                0  -> cmd.value = Command("ALREADY_REQUEST_TO_THIS_ORGANIZATION")
                1  -> cmd.value = Command("JOIN_ORGANIZATION_COMPLETED")
                2  -> cmd.value = Command("REQUEST_JOIN_SUCCESS")
            }
        }
    }

    fun logout() {
        authService.logout()
    }
}