package com.gura.face_recognition_app.api

import com.gura.face_recognition_app.model.Employee
import com.gura.face_recognition_app.model.FaceRecognitionRequest
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.model.ServerStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RecognitionAPI {

    @GET("status")
    fun checkServerStatus(): Call<ServerStatus>

    @GET("contact")
    suspend fun getAllContact(): List<Employee>

    @POST("face-recognition")
    fun startFaceRecognition(@Body faceRecognitionRequest: FaceRecognitionRequest): Call<FaceRecognitionResponse>?

}