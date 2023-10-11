package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.repository.OrganizationRepository

class AddContactActivityViewModel(val application: Application): ViewModel() {

    private val organizationRepository = OrganizationRepository(application)

    fun addContact() {
    }
}