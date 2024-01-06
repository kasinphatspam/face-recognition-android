package com.gura.face_recognition_app.repository

import android.content.Context
import android.util.Log
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.model.User

class OrganizationRepository(val context: Context) {

    interface OrganizationInformationInterface {
        fun onResponse(data: Organization)
        fun onFailure(error: String)
    }
    interface GetContactListInterface {
        fun onResponse(list: List<Contact>)
    }

    interface GetEmployeeInterface {
        fun onResponse(list: List<User>)
    }
    interface RequestJoinInterface {
        fun onJoinSuccess()
        fun onRequestSuccess()
        fun onAlreadyRequest()
        fun onFailure()
    }

    private val api = RetrofitHelper
        .getInstance(context)
        .create(BackendAPI::class.java)

    suspend fun requestJoinOrgWithPasscode(
        session: String,
        passcode: String,
        listener: RequestJoinInterface)
    {
        val headers = HashMap<String, String>()
        headers["session"] = session
        val response = api.join(headers, passcode)

        Log.d("OrganizationRepository", response.raw().message)

        if (response.code() == 202) {
            listener.onRequestSuccess()
            return
        }
        else if (response.code() == 200) {
            listener.onJoinSuccess()
            return
        } else {
            val httpErrorHandler = HttpErrorHandler()
            val errorResponse = httpErrorHandler.getErrorBody(response)
            when (errorResponse.message) {
                "Already request to join this organization" -> {
                    listener.onAlreadyRequest()
                    return
                }
                "Wrong passcode" -> {
                    listener.onFailure()
                    return
                }
            }
        }
    }

    suspend fun getCurrentOrganization(listener: OrganizationInformationInterface) {
        val userId = App.instance.userId
        val response = api.getCurrentOrganization(userId!!)

        if (response.isSuccessful) {
            listener.onResponse(response.body()!!.organization)
        }else{
            Log.e("OrganizationRepository", response.raw().toString())
            listener.onFailure(response.raw().message)
        }
    }

    suspend fun getContactInOrganization(listener: GetContactListInterface) {
        val userId = App.instance.userId
        val organizationResponse = api.getCurrentOrganization(userId!!)
        if (organizationResponse.isSuccessful) {
            val contactResponse = api.getContactInOrganization(
                organizationResponse.body()!!.organization.id
            )

            if (contactResponse.isSuccessful) {
                listener.onResponse(contactResponse.body()!!)
            }
        }
    }

    suspend fun getEmployeeInOrganization(listener: GetEmployeeInterface) {
        val userId = App.instance.userId
        val organizationResponse = api.getCurrentOrganization(userId!!)
        if (organizationResponse.isSuccessful) {
            val employeeResponse = api.getEmployee(
                organizationResponse.body()!!.organization.id
            )

            if(employeeResponse.isSuccessful) {
                listener.onResponse(employeeResponse.body()!!)
            }
        }
    }
}