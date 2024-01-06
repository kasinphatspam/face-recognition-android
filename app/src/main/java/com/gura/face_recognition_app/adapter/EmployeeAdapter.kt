package com.gura.face_recognition_app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gura.face_recognition_app.view.activity.EncodeContactActivity
import com.gura.face_recognition_app.viewmodel.ItemsViewModel
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.data.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EmployeeAdapter(private val context: Context,
                      private val mList: List<User>): RecyclerView.Adapter<EmployeeAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_contact, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.nameTextView.text = "${item.firstname} ${item.lastname}"
        holder.companyTextView.text = item.organization!!.name
        Picasso.with(context).load(item.image).placeholder(R.drawable.default_user).into(holder.profileImageView)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val companyTextView: TextView = itemView.findViewById(R.id.companyTextView)
    }
}