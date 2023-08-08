package com.gura.face_recognition_app

import android.app.Application
import android.content.res.Resources

class App: Application() {

    var userId: Int? = null
    companion object {
        val instance = App()
    }

}