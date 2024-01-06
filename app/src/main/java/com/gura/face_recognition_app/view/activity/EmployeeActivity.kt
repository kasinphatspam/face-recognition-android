package com.gura.face_recognition_app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.adapter.EmployeeAdapter
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.helper.WindowHelper
import com.gura.face_recognition_app.viewmodel.AppViewModelFactory
import com.gura.face_recognition_app.viewmodel.EmployeeActivityViewModel
import com.gura.face_recognition_app.viewmodel.LoginActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeeActivity : AppCompatActivity() {

    private lateinit var viewModel: EmployeeActivityViewModel
    private lateinit var factory: AppViewModelFactory
    private lateinit var recyclerView: RecyclerView
    private lateinit var data: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        val window = WindowHelper(this, window)
        window.statusBarColor = R.color.white
        window.allowNightMode = false
        window.publish()

        factory = AppViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[EmployeeActivityViewModel::class.java]

        data = ArrayList()
        recyclerView = findViewById(R.id.employeeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getEmployee()
        }

        viewModel.employee.observe(this) { it ->
            it.forEach {
                data.add(it)
            }
            val adapter = EmployeeAdapter(this, data)
            recyclerView.adapter = adapter
        }
    }
}