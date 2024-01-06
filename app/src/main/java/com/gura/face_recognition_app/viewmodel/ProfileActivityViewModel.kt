package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.repository.UserRepository
import retrofit2.Response

class ProfileActivityViewModel(application: Application): ViewModel() {

    private val userRepository: UserRepository = UserRepository(application)
    private val organizationRepository: OrganizationRepository = OrganizationRepository(application)
    val currentUser = MutableLiveData<User>()
    val organization = MutableLiveData<Organization>()
    suspend fun loadUserAsync() {
        userRepository.loadCurrentUserAsync(listener)
    }

    suspend fun loadOrganizationAsync() {
        organizationRepository.getCurrentOrganization(listener2)
    }

    private val listener = object : UserRepository.GetUserInterface {
        override fun onResponse(response: Response<User>) {
            if (response.isSuccessful) {
                currentUser.apply {
                    value = response.body()
                }
            }
        }

        override fun onFailure(error: String) {}
    }

    private val listener2 = object : OrganizationRepository.OrganizationInformationInterface {
        override fun onResponse(data: Organization) {
            organization.apply {
                value = data
            }
        }
        override fun onFailure(error: String) {}
    }

}