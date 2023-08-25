package com.gura.face_recognition_app.helper

import android.content.Context
import com.gura.face_recognition_app.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    fun getInstance(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS).build()
        return Retrofit.Builder().baseUrl("http://${context.getString(R.string.backend_server_ip)}")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}