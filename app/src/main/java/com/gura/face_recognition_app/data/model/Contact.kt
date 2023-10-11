package com.gura.face_recognition_app.data.model

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("id")
    var id: Int,
    @SerializedName("organization")
    var organization: Organization,
    @SerializedName("firstname")
    var firstname: String,
    @SerializedName("lastname")
    var lastname: String,
    @SerializedName("contactCompany")
    var contactCompany: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("officePhone")
    var officePhone: String,
    @SerializedName("mobile")
    var mobile: String,
    @SerializedName("email1")
    var email1: String,
    @SerializedName("email2")
    var email2: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("contactOwner")
    var contactOwner: String,
    @SerializedName("createdTime")
    var createdTime: String,
    @SerializedName("modifiedTime")
    var modifiedTime: String,
    @SerializedName("lineId")
    var lineId: String,
    @SerializedName("facebook")
    var facebook: String,
    @SerializedName("linkedin")
    var linkedin: String,
    @SerializedName("image")
    var image: String
)