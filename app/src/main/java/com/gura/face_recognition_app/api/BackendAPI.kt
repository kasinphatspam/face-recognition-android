package com.gura.face_recognition_app.api

import com.gura.face_recognition_app.model.AuthLoginRequest
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.model.AuthRegisterRequest
import com.gura.face_recognition_app.model.AuthRegisterResponse
import com.gura.face_recognition_app.model.Contact
import com.gura.face_recognition_app.model.EncodeContactImageResponse
import com.gura.face_recognition_app.model.FaceRecognitionRequest
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.model.OrganizationResponse
import com.gura.face_recognition_app.model.ServerStatus
import com.gura.face_recognition_app.model.UserInformationResponse
import retrofit2.Call
import retrofit2.Callback
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
        @Body body: AuthLoginRequest
    ): Response<AuthLoginResponse>?

    @POST("auth/register")
    suspend fun register(
        @Body body: AuthRegisterRequest
    ): Response<AuthRegisterResponse>?

    @POST("auth/forgot-password")
    fun forgotPassword()

    /*-------------------- User API Declaration --------------------*/
    @GET("user/all")
    fun getAllUser()

    @GET("user/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int
    ): Response<UserInformationResponse>

    @GET("user/{userId}/organization")
    suspend fun getCurrentOrganization(
        @Path("userId") userId: Int
    ): Response<OrganizationResponse>

    @PUT("user")
    fun update()

    @PUT("user/image")
    fun updateImageProfile()

    @DELETE("user")
    fun deleteUser()

    /*-------------------- Organization API Declaration --------------------*/
    @GET("organization/{organizationId}")
    suspend fun getOrganizationById(
        @Path("organizationId") organizationId: Int
    ): Response<OrganizationResponse>

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
        @Body faceRecognitionRequest: FaceRecognitionRequest
    ): Response<FaceRecognitionResponse>

    @PUT("organization/{organizationId}/contact/{contactId}/encode")
    suspend fun encodeContactImage(
        @Path("organizationId") organizationId: Int,
        @Path("contactId") contactId: Int,
        @Body faceRecognitionRequest: FaceRecognitionRequest
        ): Response<EncodeContactImageResponse>

    /*-------------------- Server Status API Declaration --------------------*/
    @GET("status")
    fun checkServerStatus(): Call<ServerStatus>
}