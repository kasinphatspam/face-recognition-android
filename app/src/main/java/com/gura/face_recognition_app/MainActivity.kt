package com.gura.face_recognition_app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.gura.face_recognition_app.fragment.ContactFragment
import com.gura.face_recognition_app.fragment.DashboardFragment
import me.ibrahimsn.lib.SmoothBottomBar


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = ContextCompat.getColor(this, R.color.white_blue)

        val bottomBar : SmoothBottomBar = findViewById(R.id.bottomBar)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<DashboardFragment>(R.id.fragment_container_view)
            }
        }

        bottomBar.onItemSelected = {
            if(it == 0){
                replaceFragment(DashboardFragment())
            }else if(it == 1){
                replaceFragment(ContactFragment())
            }
        }

        bottomBar.onItemReselected = {
        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}