package com.gura.face_recognition_app.adapter

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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ContactAdapter(private val context: Context,
    private val mList: List<ItemsViewModel>): RecyclerView.Adapter<ContactAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_contact, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemViewModel = mList[position]

        holder.nameTextView.text = itemViewModel.name
        holder.companyTextView.text = itemViewModel.company
        holder.profileImageView.setOnClickListener {
            val intent = Intent(context, EncodeContactActivity::class.java)
            intent.putExtra("contactId",itemViewModel.id)
            context.startActivity(intent)
        }
        Picasso.with(context).load(itemViewModel.imageUrl).placeholder(R.drawable.default_user).into(holder.profileImageView)
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