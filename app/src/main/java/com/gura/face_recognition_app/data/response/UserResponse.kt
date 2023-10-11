package com.gura.face_recognition_app.data.response

import com.google.gson.annotations.SerializedName

data class UpdateUserResponse (
    @SerializedName("message")
    var message: String
)
