package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.data.response.OrganizationResponse
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.repository.UserRepository
import retrofit2.Response

class ShareFragmentViewModel(private val application: Application): ViewModel() {

    private val userRepository: UserRepository = UserRepository(application)
    private val organizationRepository: OrganizationRepository = OrganizationRepository(application)
    val currentUser = MutableLiveData<User>()
    val organization = MutableLiveData<Organization>()
    val contactList =  MutableLiveData<List<Contact>>()

    suspend fun loadUserAsync() {
        userRepository.loadCurrentUserAsync(listener)
    }

    suspend fun loadCustomerContact() {
        organizationRepository.getContactInOrganization(listener2)
    }

    suspend fun loadOrganizationAsync() {
        organizationRepository.getCurrentOrganization(listener3)
    }

    // Get user interface
    private val listener = object : UserRepository.GetUserInterface {
        override fun onResponse(response: Response<User>) {
            if (response.isSuccessful) {
                currentUser.apply {
                    value = response.body()
                }
            }
            Log.e("DashboardViewModel", response.raw().message)
        }

        override fun onFailure(error: String) {
            TODO("Not yet implemented")
        }
    }

    // Get organization contact
    private val listener2 = object : OrganizationRepository.GetContactListInterface {
        override fun onResponse(list: List<Contact>) {
            contactList.apply {
                value = list
            }
        }
    }

    // Get current organization
    private val listener3 = object : OrganizationRepository.OrganizationInformationInterface {
        override fun onResponse(data: Organization) {
            organization.apply {
                value = data
            }
        }
        override fun onFailure(error: String) {}
    }

}