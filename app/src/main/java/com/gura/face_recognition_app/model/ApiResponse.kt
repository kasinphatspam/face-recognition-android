package com.gura.face_recognition_app.model

import com.google.gson.annotations.SerializedName

data class ServerStatus (
    @SerializedName("status")
    var status: String? = null
)
data class FaceRecognitionRequest(
    @SerializedName("imageBase64")
    var imageBase64: String,
    @SerializedName("cameraSide")
    var cameraSide: String
)

data class FaceRecognitionResponse(
    @SerializedName("name")
    var name: String?  = null,
    @SerializedName("accuracy")
    var accuracy: String? = null,
    @SerializedName("timestamp")
    var timeStamp: String
)
