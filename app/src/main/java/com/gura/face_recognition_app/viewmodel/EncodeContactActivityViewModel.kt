package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.Command
import com.gura.face_recognition_app.recognition.RecognitionHelper
import com.gura.face_recognition_app.recognition.RecognitionRepository
import com.gura.face_recognition_app.recognition.model.EncodeContactImageResponse

class EncodeContactActivityViewModel(private val application: Application): ViewModel() {

    private val userId = App.instance.userId
    var cmd = MutableLiveData<Command>()

    suspend fun send(bitmap: Bitmap, contactId: Int) {
        val recognitionHelper = RecognitionHelper(application)
        val image = rescaleBitmap(rotateBitmap(bitmap, 90f))
        recognitionHelper.load(image)
        recognitionHelper.setTarget(userId!!, contactId)
        recognitionHelper.train(listener)
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(degree)

        return Bitmap
            .createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
    }

    private fun rescaleBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap
            .createScaledBitmap(
                bitmap,
                500,
                500,
                true
            )
    }

    private val listener = object : RecognitionRepository.EncodeInterface {
        override fun onResponse(encodeContactImageResponse: EncodeContactImageResponse) {
            if(encodeContactImageResponse.encodedId == (-1).toString()){
                cmd.apply {
                    value = Command("FACE_NOT_FOUND")
                }
            } else {
                cmd.apply {
                    value = Command("ENCODE_SUCCESS")
                }
            }
        }
    }
}