package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.repository.OrganizationRepository

class JoinOrganizationActivityViewModel(private val application: Application): ViewModel() {

    private val organizationRepository = OrganizationRepository(application)
    private val userId = App.instance.userId
    var cmd = MutableLiveData<Command>()

    suspend fun commit(passcode: String) {
        organizationRepository.joinOrganizationWithPasscode(userId!!, passcode, listener)
    }

    private val listener = object: OrganizationRepository.JoinOrganizationInterface {
        override fun onResponse() {
            cmd.apply {
                value = Command("JOIN_ORGANIZATION_SUCCESS")
            }
        }
    }
}