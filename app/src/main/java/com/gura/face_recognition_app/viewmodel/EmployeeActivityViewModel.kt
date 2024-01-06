package com.gura.face_recognition_app.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.repository.OrganizationRepository

class EmployeeActivityViewModel(val application: Application): ViewModel() {

    private val organizationRepository = OrganizationRepository(application)
    var employee = MutableLiveData<List<User>>()
    suspend fun getEmployee() {
        organizationRepository.getEmployeeInOrganization(object: OrganizationRepository.GetEmployeeInterface{
            override fun onResponse(list: List<User>) {
                employee.apply {
                    value = list
                }
            }

        })
    }
}