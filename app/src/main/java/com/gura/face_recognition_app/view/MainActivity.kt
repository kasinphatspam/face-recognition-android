package com.gura.face_recognition_app.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gura.face_recognition_app.JoinOrganizationActivity
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.fragment.ContactFragment
import com.gura.face_recognition_app.fragment.DashboardFragment
import com.gura.face_recognition_app.helper.DisplayComponentHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.SmoothBottomBar


class MainActivity : AppCompatActivity(), DashboardFragment.SearchClickListener {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var factory: AppViewModelFactory

    private lateinit var bottomBar: SmoothBottomBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize helper for customizing display component
        val displayComponentHelper = DisplayComponentHelper(this@MainActivity, window)
        displayComponentHelper.changeStatusBarColor(R.color.white)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // View Model instance
        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[MainActivityViewModel::class.java]

        // Check if the account has joined company
        lifecycleScope.launch {
            viewModel.checkedCompany()
        }

        viewModel.checkOrganizationIsEmpty.observe(this@MainActivity){
            if(it){
                val intent = Intent(this,JoinOrganizationActivity::class.java)
                finish()
                startActivity(intent)
            }
        }

        // Initialize the layout variable with view id
        bottomBar = findViewById(R.id.bottomBar)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<DashboardFragment>(R.id.fragment_container_view)
            }
        }

        // Set sequence when item of bottom sheet is selected
        bottomBar.onItemSelected = {
            if (it == 0) {
                replaceFragment(DashboardFragment())
            } else if (it == 1) {
                replaceFragment(ContactFragment())
            }
        }
    }

    // Change fragment is displayed on FrameLayout
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            setReorderingAllowed(true)
        }
    }

    // Call new fragment and pass data to next fragment
    private fun replaceFragmentWithSearchClick(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putBoolean("SearchClick", true)
        fragment.arguments = bundle
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            setReorderingAllowed(true)
        }
        bottomBar.itemActiveIndex = 1
    }

    // Call special fragment while clicking search textview
    override fun onClick() {
        replaceFragmentWithSearchClick(ContactFragment())
    }
}