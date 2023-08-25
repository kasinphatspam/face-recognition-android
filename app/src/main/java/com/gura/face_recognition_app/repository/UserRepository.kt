package com.gura.face_recognition_app.repository

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.gura.face_recognition_app.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.model.UserInformationResponse
import retrofit2.Response

class UserRepository(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    interface UserInformationInterface {
        fun onCompleted(response: Response<UserInformationResponse>)
    }

    interface CheckOrganizationInterface {
        fun isEmpty()
    }
    suspend fun getCurrentUser(listener: UserInformationInterface) {
        val authRepository = AuthRepository(context)
        val response = api.getUserById(authRepository.currentUser())

        if (response.isSuccessful) {
            listener.onCompleted(response)
        }
    }

    // Check if employees have joined the organization.
    suspend fun checkOrganizationIsEmpty(listener: CheckOrganizationInterface) {
        val authRepository = AuthRepository(context)
        val response = api.getUserById(authRepository.currentUser())

        if (response.isSuccessful) {
            Log.d("CheckIsEmpty",response.body()!!.organizationId.toString())
            if(TextUtils.isEmpty(response.body()!!.organizationId.toString()) ||
                response.body()!!.organizationId.toString() == "0" ||
                response.body()!!.organizationId.toString() == "null"){
                listener.isEmpty()
            }
        }
    }

}