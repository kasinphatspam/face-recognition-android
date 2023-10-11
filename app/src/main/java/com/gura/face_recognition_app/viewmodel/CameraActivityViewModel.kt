package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.RecognitionCommand
import com.gura.face_recognition_app.recognition.model.FaceRecognitionResponse
import com.gura.face_recognition_app.recognition.RecognitionHelper
import com.gura.face_recognition_app.recognition.RecognitionRepository

class CameraActivityViewModel(val application: Application) : ViewModel() {

    private val app = App.instance
    var recognitionCommand = MutableLiveData<RecognitionCommand>()

    suspend fun sendImageForRecognizing(uri: Uri) {

        val bitmap: Bitmap = MediaStore
            .Images.Media
            .getBitmap(application.contentResolver, uri)

        // Scale image down from original size to 500 * 500
        val scaledBitmap = Bitmap
            .createScaledBitmap(bitmap, 500, 500, true)

        // Rotate image
        val matrix = Matrix()
        matrix.setRotate(270f)
        val rotatedBitmap = Bitmap
            .createBitmap(
                scaledBitmap, 0, 0, scaledBitmap.width,
                scaledBitmap.height, matrix, true
            )

        val recognitionHelper = RecognitionHelper(application)
        recognitionHelper.load(rotatedBitmap)
        recognitionHelper.setTarget(app.userId!!, null)

        recognitionHelper.recognize(
            object : RecognitionRepository.RecognitionInterface {
                override fun onResponse(faceRecognitionResponse: FaceRecognitionResponse) {
                    recognitionCommand.apply {
                        value = RecognitionCommand(
                            "RECOGNITION_COMPLETED",
                            faceRecognitionResponse.statusCode, faceRecognitionResponse
                        )
                    }
                }
            }
        )
    }
}