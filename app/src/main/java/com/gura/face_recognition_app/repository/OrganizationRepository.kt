package com.gura.face_recognition_app.repository

import android.content.Context
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.response.OrganizationResponse
import retrofit2.Response

class OrganizationRepository(val context: Context) {

    interface OrganizationInformationInterface {
        fun onResponse(data: Organization)
        fun onFailure(error: String)
    }
    interface GetContactListInterface {
        fun onResponse(list: List<Contact>)
    }
    interface JoinOrganizationInterface {
        fun onResponse()
    }

    private val api = RetrofitHelper
        .getInstance(context)
        .create(BackendAPI::class.java)

    suspend fun join(userId: Int, passcode: String, listener: JoinOrganizationInterface) {
        api.join(userId, passcode)
        listener.onResponse()
    }

    suspend fun getCurrentOrganization(listener: OrganizationInformationInterface) {
        val userId = App.instance.userId
        val response = api.getCurrentOrganization(userId!!)

        if (response.isSuccessful) {
            listener.onResponse(response.body()!!.organization)
        }else{
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
}