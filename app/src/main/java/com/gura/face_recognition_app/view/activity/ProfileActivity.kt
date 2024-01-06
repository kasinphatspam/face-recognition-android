package com.gura.face_recognition_app.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.ProfileActivityViewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var circleImageView: CircleImageView
    private lateinit var factory: AppViewModelFactory
    private lateinit var viewModel: ProfileActivityViewModel
    private lateinit var nameTextView: TextView
    private lateinit var organizationNameTextView: TextView
    private lateinit var closeImageButton: ImageButton

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[ProfileActivityViewModel::class.java]

        circleImageView = findViewById(R.id.circleImageView)
        nameTextView = findViewById(R.id.nameTextView)
        organizationNameTextView = findViewById(R.id.organizationNameTextView)
        closeImageButton = findViewById(R.id.closeImageButton)

        lifecycleScope.launch {
            viewModel.loadUserAsync()
            viewModel.loadOrganizationAsync()
        }

        viewModel.currentUser.observe(this) {
            nameTextView.text = "${it.firstname} ${it.lastname}"
            Picasso.with(this)
                .load(it.image)
                .placeholder(R.drawable.default_user)
                .into(circleImageView)
        }

        viewModel.organization.observe(this) {
            organizationNameTextView.text = it.name
        }

        closeImageButton.setOnClickListener {
            finish()
        }
    }
}