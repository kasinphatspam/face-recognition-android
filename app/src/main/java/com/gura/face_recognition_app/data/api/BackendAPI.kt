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
import com.gura.face_recognition_app.data.response.MessageResponse
import com.gura.face_recognition_app.data.response.OrganizationResponse
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.data.response.ServerStatus
import com.gura.face_recognition_app.data.response.UpdateUserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackendAPI {

    /*--------------------  Auth API Declaration  --------------------*/
    @GET("auth/me")
    suspend fun me(@HeaderMap headers: Map<String, String>): Response<User>

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
    @GET("users")
    fun getAllUser()

    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int
    ): Response<User>

    @GET("users/{userId}/organization")
    suspend fun getCurrentOrganization(
        @Path("userId") userId: Int
    ): Response<OrganizationResponse>

    @PUT("users")
    fun update(id: Int, data: User): Response<UpdateUserResponse>

    @PUT("users/image")
    fun updateImageProfile()

    @DELETE("users")
    fun deleteUser()

    /*-------------------- Organization API Declaration --------------------*/
    @GET("organization/{organizationId}")
    suspend fun getOrganizationById(
        @Path("organizationId") organizationId: Int
    ): Response<Organization>

    @POST("organizations/join/{passcode}")
    suspend fun join(
        @HeaderMap headers: Map<String, String>,
        @Path("passcode") passcode: String
    ): Response<MessageResponse>

    @GET("organizations/info/contacts")
    suspend fun getContactInOrganization(
        @Path("organizationId") organizationId: Int,
    ): Response<List<Contact>>

    @GET("organizations/info/employees")
    suspend fun getEmployee(
        @Path("organizationId") organizationId: Int,
    ): Response<List<User>>

    @POST("organizations/info/contacts/recognition")
    suspend fun startFaceRecognition(
        @Path("organizationId") organizationId: Int,
        @Path("userId") userId: Int,
        @Body recognitionRequest: RecognitionRequest
    ): Response<FaceRecognitionResponse>

    @PUT("organizations/info/contacts/{contactId}/encode")
    suspend fun encodeContactImage(
        @Path("organizationId") organizationId: Int,
        @Path("contactId") contactId: Int,
        @Body recognitionRequest: RecognitionRequest
        ): Response<EncodeContactImageResponse>

    /*-------------------- Server Status API Declaration --------------------*/
    @GET("status")
    fun checkServerStatus(): Call<ServerStatus>
}