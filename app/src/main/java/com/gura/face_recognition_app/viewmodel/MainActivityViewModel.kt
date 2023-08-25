package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.JoinOrganizationActivity
import com.gura.face_recognition_app.model.Organization
import com.gura.face_recognition_app.repository.UserRepository


class MainActivityViewModel(private val application: Application) :
    ViewModel() {

    private val userRepository = UserRepository(application)
    var checkOrganizationIsEmpty = MutableLiveData<Boolean>(false)

    // Check if the account have joined company
    suspend fun checkedCompany(){
        return userRepository.checkOrganizationIsEmpty(listener)
    }

    private val listener = object: UserRepository.CheckOrganizationInterface{
        override fun isEmpty() {
            checkOrganizationIsEmpty.apply {
                value = true
            }
        }
    }
}