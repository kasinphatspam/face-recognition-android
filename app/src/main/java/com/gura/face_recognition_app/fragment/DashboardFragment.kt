package com.gura.face_recognition_app.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.SettingActivity
import com.gura.face_recognition_app.model.OrganizationResponse
import com.gura.face_recognition_app.model.UserInformationResponse
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.repository.UserRepository
import com.gura.face_recognition_app.view.CameraActivity
import com.gura.face_recognition_app.view.RealtimeCameraActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response

class DashboardFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var nameTextView: TextView
    private lateinit var searchTextView: TextView
    private lateinit var passcodeTextView: TextView
    private lateinit var profileCircleImageView: CircleImageView
    private lateinit var constraintLayout: ConstraintLayout

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

        val userRepository = UserRepository(context)
        val organizationRepository = OrganizationRepository(context)

        lifecycleScope.launch {

            userRepository.getCurrentUser(object : UserRepository.UserInformationInterface {
                @SuppressLint("SetTextI18n")
                override fun onCompleted(response: Response<UserInformationResponse>) {
                    nameTextView.text = "Welcome, ${response.body()!!.firstname}"
                    constraintLayout.visibility = View.VISIBLE

                    Log.e("ImageAPI", response.body()!!.profileImage)
                    Picasso.with(context)
                        .load(response.body()!!.profileImage)
                        .placeholder(R.drawable.default_user)
                        .into(profileCircleImageView)
                }

            })

            organizationRepository.getOrganization(
                object : OrganizationRepository.OrganizationInformationInterface {
                    @SuppressLint("SetTextI18n")
                    override fun onCompleted(response: Response<OrganizationResponse>) {
                        passcodeTextView.text = Html
                            .fromHtml("Your company passcode is \u0022${response.body()!!.organization.code}\u0022")

                    }
                }
            )
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