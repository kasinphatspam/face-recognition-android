package com.gura.face_recognition_app.service

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.gura.face_recognition_app.api.RecognitionAPI
import com.gura.face_recognition_app.api.RetrofitHelper
import com.gura.face_recognition_app.model.FaceRecognitionRequest
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.model.ServerStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.Base64


class RecognitionService(context: Context) {

    interface RecognitionInterface {
        fun onCompleted(faceRecognitionResponse: FaceRecognitionResponse)
    }

    interface ServerInterface {
        fun onConnected()
        fun onDisconnected(msg: String)
    }

    private val api = RetrofitHelper.getInstance(context).create(RecognitionAPI::class.java)

    fun startFaceRecognition(base64: String, cameraSide: String, listener: RecognitionInterface) {

        val faceRecognitionRequest = FaceRecognitionRequest(base64,cameraSide)
        val status = api.startFaceRecognition(faceRecognitionRequest)

        status!!.enqueue(object : Callback<FaceRecognitionResponse> {
            override fun onResponse(
                call: Call<FaceRecognitionResponse>,
                response: Response<FaceRecognitionResponse>
            ) {
                if (response.isSuccessful) {
                    listener.onCompleted(response.body()!!)
                    Log.d("API", response.body().toString())
                }
            }

            override fun onFailure(call: Call<FaceRecognitionResponse>, t: Throwable) {
                Log.e("API", t.message.toString())
            }
        })
    }

    fun getAllContact(){
        GlobalScope.launch {
            api.getAllContact()
        }
    }

    fun checkServerStatus(listener: ServerInterface){
        GlobalScope.launch {
            val status = api.checkServerStatus()
            status.enqueue(object: Callback<ServerStatus>{
                override fun onResponse(call: Call<ServerStatus>, response: Response<ServerStatus>) {
                    if(response.isSuccessful){
                        Log.d("API",response.body().toString())
                        listener.onConnected()
                    }
                }

                override fun onFailure(call: Call<ServerStatus>, t: Throwable) {
                    Log.e("API",t.message.toString())
                    listener.onDisconnected(t.message.toString())
                }

            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertImageToBase64(bitmap: Bitmap): String{

        // convert ByteArray to Base64
        fun ByteArray.toBase64(): String =
            String(Base64.getEncoder().encode(this))

        val byteArrayOutputStream = ByteArrayOutputStream()
        // resize image for converting to base64
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray().toBase64()
    }
}