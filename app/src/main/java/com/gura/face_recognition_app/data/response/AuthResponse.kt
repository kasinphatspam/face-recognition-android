package com.gura.face_recognition_app.data.response

import com.google.gson.annotations.SerializedName
import com.gura.face_recognition_app.data.model.User

data class LoginResponse (
    @SerializedName("message")
    var message: String,
    @SerializedName("id")
    var id: Int
)

data class RegisterResponse(
    @SerializedName("message")
    var message: String,
    @SerializedName("user")
    var user: User
)