package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.RecognitionCommand
import com.gura.face_recognition_app.model.FaceRecognitionResponse
import com.gura.face_recognition_app.repository.RecognitionRepository

class RealtimeCameraActivityViewModel(private val application: Application): ViewModel() {

    private val recognitionRepository = RecognitionRepository(application)
    private val app = App.instance
    var recognitionCommand = MutableLiveData<RecognitionCommand>()

    suspend fun sendImageForRecognizing(bitmap: Bitmap) {
        val base64 = recognitionRepository.convertImageToBase64(bitmap)
        recognitionRepository.startFaceRecognition(app.userId!!, base64,
            object: RecognitionRepository.RecognitionInterface{
            override fun onCompleted(faceRecognitionResponse: FaceRecognitionResponse) {
                recognitionCommand.apply {
                    value = RecognitionCommand(
                        "RECOGNITION_COMPLETED",
                        faceRecognitionResponse.statusCode, faceRecognitionResponse
                    )
                }
            }
        })
    }

    fun resizeImage(bitmap: Bitmap): InputImage{
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(bitmap.height * bitmap.width)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val scaledBitmap = Bitmap
            .createScaledBitmap(
                bitmap,
                500,
                500,
                true
            )

        return InputImage.fromBitmap(scaledBitmap, 0)
    }

    fun initializeDetector(): FaceDetector{
        val option = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
        return FaceDetection.getClient(option)
    }
}