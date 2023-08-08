package com.gura.face_recognition_app.api

import com.gura.face_recognition_app.model.UserInformationRequest
import com.gura.face_recognition_app.model.UserInformationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAPI {

    @GET("user/all")
    fun getAllUser()

    @GET("user/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): Response<UserInformationResponse>

    @PUT("user")
    fun update()

    @PUT("user/image")
    fun updateImageProfile()

    @DELETE("user")
    fun deleteUser()

}