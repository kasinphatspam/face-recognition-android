package com.gura.face_recognition_app.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gura.face_recognition_app.data.response.ErrorResponse
import com.gura.face_recognition_app.data.response.MessageResponse
import retrofit2.Response
class HttpErrorHandler {
    fun <T: Any> getErrorBody(response: Response<T>): ErrorResponse {
        val gson = Gson()
        val type = object : TypeToken<ErrorResponse>() {}.type
        val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
        return errorResponse!!
    }
}