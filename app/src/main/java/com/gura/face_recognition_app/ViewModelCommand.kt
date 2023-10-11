package com.gura.face_recognition_app

import com.gura.face_recognition_app.recognition.model.FaceRecognitionResponse

/*--------------- Login Activity ---------------*/
data class AuthCommand (
    var cmd: String
)

data class Command (
    var cmd: String
)

/*--------------- Camera Activity ---------------*/
data class RecognitionCommand (
    var cmd: String,
    var statusCode: Int,
    var response: FaceRecognitionResponse
)