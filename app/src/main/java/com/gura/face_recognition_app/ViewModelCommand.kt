package com.gura.face_recognition_app

import com.gura.face_recognition_app.model.FaceRecognitionResponse

/*--------------- Login Activity ---------------*/
data class AuthCommand (
    var cmd: String
)

/*--------------- Camera Activity ---------------*/
data class RecognitionCommand (
    var cmd: String,
    var statusCode: Int,
    var response: FaceRecognitionResponse
)