package com.gura.face_recognition_app.recognition

import android.content.Context
import android.util.Log
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.api.BackendAPI
import com.gura.face_recognition_app.recognition.model.RecognitionRequest
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.recognition.model.EncodeContactImageResponse
import com.gura.face_recognition_app.recognition.model.FaceRecognitionResponse


class RecognitionRepository(val context: Context) {

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    interface RecognitionInterface {
        fun onResponse(faceRecognitionResponse: FaceRecognitionResponse)
    }

    interface EncodeInterface {
        fun onResponse(encodeContactImageResponse: EncodeContactImageResponse)
    }

    suspend fun recognize (userId: Int, base64: String, listener: RecognitionInterface) {
        val organization = api.getCurrentOrganization(userId)

        if (organization.isSuccessful) {
            val organizationId = organization.body()!!.organization.id
            val request = RecognitionRequest(base64)
            val response = api.startFaceRecognition(organizationId, App.instance.userId!! , request)

            if (response.isSuccessful) {
                Log.d("Recognition", response.body()!!.toString())
                listener.onResponse(response.body()!!)
            }
        }
    }

    suspend fun train (userId: Int, contactId: Int, base64: String, listener: EncodeInterface) {
        val organization = api.getCurrentOrganization(userId)

        if(organization.isSuccessful){
            val organizationId = organization.body()!!.organization.id
            val request = RecognitionRequest(base64)
            val response = api.encodeContactImage(organizationId,
                contactId,request)

            if(response.isSuccessful){
                listener.onResponse(response.body()!!)
            }
        }

    }
}