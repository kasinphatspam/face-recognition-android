package com.gura.face_recognition_app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.RecognitionCommand
import com.gura.face_recognition_app.recognition.model.FaceRecognitionResponse
import com.gura.face_recognition_app.recognition.RecognitionHelper
import com.gura.face_recognition_app.recognition.RecognitionRepository

class RealtimeCameraActivityViewModel(private val application: Application): ViewModel() {

    private val app = App.instance
    var recognitionCommand = MutableLiveData<RecognitionCommand>()

    suspend fun sendImageForRecognizing(bitmap: Bitmap) {
        val recognitionHelper = RecognitionHelper(application)
        recognitionHelper.load(bitmap)
        recognitionHelper.setTarget(app.userId!!, null)
        recognitionHelper.recognize(
            object: RecognitionRepository.RecognitionInterface{
            override fun onResponse(faceRecognitionResponse: FaceRecognitionResponse) {
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