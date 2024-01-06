package com.gura.face_recognition_app.data.response

import com.google.gson.annotations.SerializedName
import com.gura.face_recognition_app.data.model.User

data class LoginResponse (
    @SerializedName("message")
    var message: String,
    @SerializedName("session")
    var session: String
)

data class RegisterResponse(
    @SerializedName("message")
    var message: String,
    @SerializedName("session")
    var session: String
)