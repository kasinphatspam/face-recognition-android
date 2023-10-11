package com.gura.face_recognition_app.recognition

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.util.Base64

class RecognitionHelper(val context: Context) {

    private var bitmap: Bitmap? = null
    private var userId: Int? = null
    private var contactId: Int? = null

    private val recognitionRepository: RecognitionRepository = RecognitionRepository(context)

    fun load(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    fun setTarget(userId: Int, contactId: Int?) {
        this.userId = userId
        this.contactId = contactId
    }

    suspend fun train(
        listener: RecognitionRepository.EncodeInterface) {
        recognitionRepository.train(
            this.userId!!,
            this.contactId!!,
            convertImageToBase64(this.bitmap!!),
            listener
        )
    }

    suspend fun recognize(
        listener: RecognitionRepository.RecognitionInterface) {
        recognitionRepository.recognize(
            this.userId!!,
            convertImageToBase64(this.bitmap!!),
            listener
        )
    }

    private fun convertImageToBase64(bitmap: Bitmap): String {
        // convert ByteArray to Base64
        fun ByteArray.toBase64(): String =
            String(Base64.getEncoder().encode(this))

        val byteArrayOutputStream = ByteArrayOutputStream()
        // resize image for converting to base64
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray().toBase64()
    }

}