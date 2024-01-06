package com.gura.face_recognition_app

import android.app.Application
import com.gura.face_recognition_app.data.model.User

class App: Application() {

    var userId: Int? = null
    var user: User? = null

    companion object {
        val instance = App()
    }

}