package com.gura.face_recognition_app.repository

import android.content.Context
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.model.Contact
import com.gura.face_recognition_app.model.OrganizationResponse
import retrofit2.Response

class OrganizationRepository(val context: Context) {

    interface OrganizationInformationInterface {
        fun onCompleted(response: Response<OrganizationResponse>)
    }
    interface GetContactListInterface {
        fun onCompleted(list: List<Contact>)
    }
    interface JoinOrganizationInterface {
        fun onCompleted()
    }

    private val api = RetrofitHelper
        .getInstance(context)
        .create(BackendAPI::class.java)

    suspend fun join(userId: Int, passcode: String, listener: JoinOrganizationInterface) {
        api.join(userId, passcode)
        listener.onCompleted()
    }

    suspend fun getOrganization(listener: OrganizationInformationInterface){
        val userId = App.instance.userId
        val organizationResponse = api.getCurrentOrganization(userId!!)
        if (organizationResponse.isSuccessful) {
            val organizationId = organizationResponse.body()!!.organization.organizationId

            val response = api.getOrganizationById(organizationId)

            if(response.isSuccessful){
                listener.onCompleted(response)
            }
        }
    }

    suspend fun getContactInOrganization(listener: GetContactListInterface) {
        val userId = App.instance.userId
        val organizationResponse = api.getCurrentOrganization(userId!!)
        if (organizationResponse.isSuccessful) {
            val contactResponse = api.getContactInOrganization(
                organizationResponse.body()!!.organization.organizationId
            )

            if (contactResponse.isSuccessful) {
                listener.onCompleted(contactResponse.body()!!)
            }
        }

    }
}