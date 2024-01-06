package com.gura.face_recognition_app.helper

import android.content.Context
import com.gura.face_recognition_app.R
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitHelper {
    fun getInstance(context: Context): Retrofit {
        val client = OkHttpClient.Builder()

        val accessKey: String = context.resources.getString(R.string.accessKey)
        client.addInterceptor(Interceptor { chain ->
                val request: Request =
                    chain.request()
                        .newBuilder()
                        .addHeader("access-key", accessKey)
                        .build()
                chain.proceed(request)
            })
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        return Retrofit.Builder()
            .baseUrl("http://${context.getString(R.string.backend_server_ip)}")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
    }
}