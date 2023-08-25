package com.gura.face_recognition_app.model

data class Contact(
    var contactId: Int,
    var organizationId: Int,
    var firstname: String,
    var lastname: String,
    var organizationName: String,
    var title: String,
    var officePhone: String,
    var mobile: String,
    var email: String,
    var alternateEmail: String,
    var dob: String,
    var contactOwner: String,
    var createdTime: String,
    var modifiedTime: String,
    var lineId: String,
    var facebook: String,
    var linkedin: String,
    var encodedId: String,
    var image: String
)