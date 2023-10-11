package com.gura.face_recognition_app.data.response

import com.google.gson.annotations.SerializedName
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.data.model.Organization

data class ServerStatus (
    @SerializedName("message")
    var message: String
)

/*--------------------  User Model  --------------------*/

data class OrganizationResponse (
    @SerializedName("organization")
    var organization: Organization
)