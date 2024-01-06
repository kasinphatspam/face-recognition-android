package com.gura.face_recognition_app.recognition.model

import com.google.gson.annotations.SerializedName
import com.gura.face_recognition_app.data.model.Contact

data class RecognitionRequest(
    @SerializedName("image")
    var imageBase64: String
)

data class EncodeContactImageResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("encodedId")
    var encodedId: String? = null,
)

data class FaceRecognitionResponse(
    @SerializedName("accuracy")
    var accuracy: Float? = null,
    @SerializedName("checkedTime")
    var checkedTime: String,
    @SerializedName("statusCode")
    var statusCode: Int,
    @SerializedName("result")
    var contact: Contact
)