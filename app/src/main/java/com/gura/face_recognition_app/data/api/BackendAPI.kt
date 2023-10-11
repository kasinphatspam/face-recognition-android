package com.gura.face_recognition_app.data.api

import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.recognition.model.RecognitionRequest
import com.gura.face_recognition_app.data.request.LoginRequest
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.recognition.model.EncodeContactImageResponse
import com.gura.face_recognition_app.recognition.model.FaceRecognitionResponse
import com.gura.face_recognition_app.data.response.LoginResponse
import com.gura.face_recognition_app.data.response.OrganizationResponse
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.data.response.ServerStatus
import com.gura.face_recognition_app.data.response.UpdateUserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendAPI {

    /*--------------------  Auth API Declaration  --------------------*/
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>?

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<RegisterResponse>?

    @POST("auth/forgot-password")
    fun forgotPassword()

    /*-------------------- User API Declaration --------------------*/
    @GET("user/all")
    fun getAllUser()

    @GET("user/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int
    ): Response<User>

    @GET("user/{userId}/organization")
    suspend fun getCurrentOrganization(
        @Path("userId") userId: Int
    ): Response<OrganizationResponse>

    @PUT("user")
    fun update(id: Int, data: User): Response<UpdateUserResponse>

    @PUT("user/image")
    fun updateImageProfile()

    @DELETE("user")
    fun deleteUser()

    /*-------------------- Organization API Declaration --------------------*/
    @GET("organization/{organizationId}")
    suspend fun getOrganizationById(
        @Path("organizationId") organizationId: Int
    ): Response<Organization>

    @POST("organization/user/{userId}/join/{passcode}")
    suspend fun join(
        @Path("userId") userId: Int,
        @Path("passcode") passcode: String
    )

    @GET("organization/{organizationId}/contact/list/all")
    suspend fun getContactInOrganization(
        @Path("organizationId") organizationId: Int,
    ): Response<List<Contact>>

    @POST("organization/{organizationId}/contact/encode/recognition")
    suspend fun startFaceRecognition(
        @Path("organizationId") organizationId: Int,
        @Body recognitionRequest: RecognitionRequest
    ): Response<FaceRecognitionResponse>

    @PUT("organization/{organizationId}/contact/{contactId}/encode")
    suspend fun encodeContactImage(
        @Path("organizationId") organizationId: Int,
        @Path("contactId") contactId: Int,
        @Body recognitionRequest: RecognitionRequest
        ): Response<EncodeContactImageResponse>

    /*-------------------- Server Status API Declaration --------------------*/
    @GET("status")
    fun checkServerStatus(): Call<ServerStatus>
}