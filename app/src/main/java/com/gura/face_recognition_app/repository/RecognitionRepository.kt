package com.gura.face_recognition_app.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.gura.face_recognition_app.api.BackendAPI
import com.gura.face_recognition_app.helper.RetrofitHelper
import com.gura.face_recognition_app.model.EncodeContactImageResponse
import com.gura.face_recognition_app.model.FaceRecognitionRequest
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import java.io.ByteArrayOutputStream
import java.util.Base64


class RecognitionRepository(val context: Context) {

    interface RecognitionInterface {
        fun onCompleted(faceRecognitionResponse: FaceRecognitionResponse)
    }

    interface EncodeInterface {
        fun onCompleted(encodeContactImageResponse: EncodeContactImageResponse)
    }

    private val api = RetrofitHelper.getInstance(context).create(BackendAPI::class.java)

    suspend fun startFaceRecognition(userId: Int, base64: String, listener: RecognitionInterface) {
        val organization = api.getCurrentOrganization(userId)

        if (organization.isSuccessful) {
            val organizationId = organization.body()!!.organization.organizationId
            val request = FaceRecognitionRequest(base64)
            val response = api.startFaceRecognition(organizationId, request)

            if (response.isSuccessful) {
                listener.onCompleted(response.body()!!)
            }
        }
    }

    suspend fun encodeContactImage(userId: Int, contactId: Int, base64: String, listener: EncodeInterface) {
        val organization = api.getCurrentOrganization(userId)

        if(organization.isSuccessful){
            val organizationId = organization.body()!!.organization.organizationId
            val request = FaceRecognitionRequest(base64)
            val response = api.encodeContactImage(organizationId,
                contactId,request)

            if(response.isSuccessful){
                listener.onCompleted(response.body()!!)
            }
        }

    }

    fun convertImageToBase64(bitmap: Bitmap): String {
        // convert ByteArray to Base64
        fun ByteArray.toBase64(): String =
            String(Base64.getEncoder().encode(this))

        val byteArrayOutputStream = ByteArrayOutputStream()
        // resize image for converting to base64
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray().toBase64()
    }
}