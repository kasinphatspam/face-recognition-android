package com.gura.face_recognition_app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AddContactActivityViewModel
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory

class AddContactActivity : AppCompatActivity() {

    private lateinit var factory: AppViewModelFactory
    private lateinit var viewModel: AddContactActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Initialize helper for customizing display component
        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[AddContactActivityViewModel::class.java]
    }
}