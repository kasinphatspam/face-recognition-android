package com.gura.face_recognition_app.model

import com.google.gson.annotations.SerializedName

data class Organization (
    var organizationId: Int,
    var organizationName: String,
    var code: String
)