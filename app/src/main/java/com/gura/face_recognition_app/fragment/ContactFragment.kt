package com.gura.face_recognition_app.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gura.face_recognition_app.ItemsViewModel
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.adapter.ContactAdapter
import com.gura.face_recognition_app.model.Contact
import com.gura.face_recognition_app.repository.OrganizationRepository
import kotlinx.coroutines.launch


class ContactFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = container!!.context
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEditText = view.findViewById(R.id.searchEditText)
        val argument = arguments
        if(argument != null){
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        recyclerView = view.findViewById(R.id.contactRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false

        val data = ArrayList<ItemsViewModel>()
        val organizationRepository = OrganizationRepository(context)
        lifecycleScope.launch {
            organizationRepository.getContactInOrganization(
                object : OrganizationRepository.GetContactListInterface{
                    override fun onCompleted(list: List<Contact>) {
                        list.forEach {
                            data.add(
                                ItemsViewModel(
                                    it.firstname + " " + it.lastname,
                                    it.organizationName, it.image, it.contactId
                                )
                            )
                        }

                        val adapter = ContactAdapter(context,data)
                        recyclerView.adapter = adapter
                    }

                }
            )
        }
    }
}