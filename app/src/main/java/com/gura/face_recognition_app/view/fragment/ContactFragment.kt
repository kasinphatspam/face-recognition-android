package com.gura.face_recognition_app.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gura.face_recognition_app.viewmodel.ItemsViewModel
import com.gura.face_recognition_app.R
import com.gura.face_recognition_app.adapter.ContactAdapter
import com.gura.face_recognition_app.data.model.Contact
import com.gura.face_recognition_app.repository.OrganizationRepository
import com.gura.face_recognition_app.viewmodel.ShareFragmentViewModel
import kotlinx.coroutines.launch


class ContactFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private val fragmentViewModel by activityViewModels<ShareFragmentViewModel>()

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
        // detect signal of search clicked on dashboard fragment
        if(argument != null){
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        // initialize recyclerview on the fragment
        recyclerView = view.findViewById(R.id.contactRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false

        val data = ArrayList<ItemsViewModel>()
        // load contact list
        lifecycleScope.launch {
            fragmentViewModel.loadCustomerContact()
        }

        // put contact list to adapter and show it on recyclerview
        fragmentViewModel.contactList.observe(viewLifecycleOwner) { list ->
            list.forEach {
                data.add(
                    ItemsViewModel(
                        it.firstname + " " + it.lastname,
                        it.contactCompany, it.image, it.id
                    )
                )
            }
            val adapter = ContactAdapter(context, data)
            recyclerView.adapter = adapter
        }
    }
}