package com.gura.face_recognition_app.data.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id")
    var id: Int,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("personalId")
    var personalId: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("image")
    var image: String,
    @SerializedName("organization")
    var organization: Organization?,
    @SerializedName("role")
    var role: Role?
)