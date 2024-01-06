package com.gura.face_recognition_app.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.SettingActivity
import com.gura.face_recognition_app.view.activity.CameraActivity
import com.gura.face_recognition_app.view.activity.EmployeeActivity
import com.gura.face_recognition_app.view.activity.HistoryActivity
import com.gura.face_recognition_app.view.activity.ProfileActivity
import com.gura.face_recognition_app.view.activity.RealtimeCameraActivity
import com.gura.face_recognition_app.viewmodel.ShareFragmentViewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var nameTextView: TextView
    private lateinit var searchTextView: TextView
    private lateinit var passcodeTextView: TextView
    private lateinit var profileCircleImageView: CircleImageView
    private lateinit var constraintLayout: ConstraintLayout
    private val fragmentViewModel by activityViewModels<ShareFragmentViewModel>()

    interface SearchClickListener {
        fun onClick()
    }

    private lateinit var searchClickListener: SearchClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = container!!.context
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.nameTextView)
        searchTextView = view.findViewById(R.id.searchTextView)
        passcodeTextView = view.findViewById(R.id.passcodeTextView)
        constraintLayout = view.findViewById(R.id.constraintLayout)
        profileCircleImageView = view.findViewById(R.id.profileCircleImageView)

        searchTextView.setOnClickListener {
            searchClickListener.onClick()
        }

        lifecycleScope.launch {
            fragmentViewModel.loadUserAsync()
            fragmentViewModel.loadOrganizationAsync()
        }

        fragmentViewModel.currentUser.observe(viewLifecycleOwner) {
            nameTextView.text = "Welcome, ${it.firstname}"
            constraintLayout.visibility = View.VISIBLE

            Picasso.with(context)
                .load(it.image)
                .placeholder(R.drawable.default_user)
                .into(profileCircleImageView)
        }

        fragmentViewModel.organization.observe(viewLifecycleOwner) {
            passcodeTextView.text = Html
                .fromHtml("Your company passcode is \u0022${it.code}\u0022")

        }

        profileCircleImageView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }

        val startFaceRecognitionImageButton: ImageButton =
            view.findViewById(R.id.recognitionImageButton)

        val historyImageButton: ImageButton =
            view.findViewById(R.id.historyImageButton)

        val employeeImageButton: ImageButton =
            view.findViewById(R.id.employeeImageButton)

        val settingImageButton: ImageButton =
            view.findViewById(R.id.settingImageButton)

        startFaceRecognitionImageButton.setOnClickListener {
            val intent = Intent(context, RealtimeCameraActivity::class.java)
            startActivity(intent)
        }

        settingImageButton.setOnClickListener {
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }

        employeeImageButton.setOnClickListener {
            val intent = Intent(context, EmployeeActivity::class.java)
            startActivity(intent)
        }

        historyImageButton.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchClickListener = context as SearchClickListener
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.cancel()
    }
}