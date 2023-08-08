package com.gura.face_recognition_app.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ServerStatus (
    @SerializedName("message")
    var message: String
)
data class ServerResponse (
    @SerializedName("message")
    var message: String
)

data class FaceRecognitionRequest(
    @SerializedName("imageBase64")
    var imageBase64: String
)

data class FaceRecognitionResponse(
    @SerializedName("name")
    var name: String?  = null,
    @SerializedName("accuracy")
    var accuracy: String? = null,
    @SerializedName("timestamp")
    var timeStamp: String
    )

data class AuthLoginRequest (
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String
    )

data class AuthLoginResponse (
    @SerializedName("message")
    var message: String,
    @SerializedName("userId")
    var userId: Int)

data class AuthRegisterRequest (
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("personalId")
    var personalId: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("profileImage")
    var profile_image: String
    )

data class AuthRegisterResponse(
    @SerializedName("message")
    var message: String,
    @SerializedName("userId")
    var userId: Int
    )

data class UserInformationRequest (
    @SerializedName("userId")
    var userId: Int
)
data class UserInformationResponse (
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("personalId")
    var personalId: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("profileImage")
    var profile_image: String
)