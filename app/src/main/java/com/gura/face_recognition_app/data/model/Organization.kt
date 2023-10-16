package com.gura.face_recognition_app.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Organization (
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("passcode")
    var code: String,
    @SerializedName("codeCreatedTime")
    var codCreatedTime: Date,
)