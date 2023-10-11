package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.JoinOrganizationActivity
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.repository.UserRepository


class MainActivityViewModel(private val application: Application) :
    ViewModel() {

    private val organizationRepository = OrganizationRepository(application)
    val command = MutableLiveData<Command>()

    suspend fun getCurrentOrganization() {
        organizationRepository.getCurrentOrganization(listener)
    }

    private val listener = object: OrganizationRepository.OrganizationInformationInterface {
        override fun onResponse(data: Organization) {
            command.apply {
                value = Command("FOUND_ORGANIZATION")
            }
        }

        override fun onFailure(error: String) {
            command.apply {
                value = Command("NOT_FOUND_ORGANIZATION")
            }
        }
    }

}