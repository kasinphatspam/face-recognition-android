package com.gura.face_recognition_app.api

import com.gura.face_recognition_app.model.AuthLoginRequest
import com.gura.face_recognition_app.model.AuthLoginResponse
import com.gura.face_recognition_app.model.AuthRegisterRequest
import com.gura.face_recognition_app.model.AuthRegisterResponse
import com.gura.face_recognition_app.model.ServerStatus
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthAPI {

    @GET("status")
    fun checkServerStatus(): Call<ServerStatus>

    @POST("auth/login")
    suspend fun login(@Body body: AuthLoginRequest): Response<AuthLoginResponse>?

    @POST("auth/register")
    suspend fun register(@Body body: AuthRegisterRequest): Response<AuthRegisterResponse>?

    @POST("auth/forgot-password")
    fun forgotPassword()
}