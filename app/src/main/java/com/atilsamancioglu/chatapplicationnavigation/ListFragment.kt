package com.atilsamancioglu.chatapplicationnavigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.atilsamancioglu.chatapplicationnavigation.databinding.FragmentListBinding
import com.atilsamancioglu.chatapplicationnavigation.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp


class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListRecyclerAdapter
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private var chats = arrayListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListRecyclerAdapter()
        binding.listRecyclerView.adapter = adapter
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.button.setOnClickListener {

            val chatText = binding.chatText.text.toString()
            val user = auth.currentUser!!.email!!

            val dataMap = HashMap<String, Any>()
            dataMap.put("text",chatText)
            dataMap.put("user",user)
            dataMap.put("date",FieldValue.serverTimestamp())

            firestore.collection("Chats").add(dataMap).addOnSuccessListener {
                binding.chatText.setText("")
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                binding.chatText.setText("")
            }
        }

        firestore.collection("Chats").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if (value != null) {
                if(value!!.isEmpty) {
                    Toast.makeText(requireContext(),"No Chat",Toast.LENGTH_LONG).show()
                } else {
                    val documents = value.documents
                    chats.clear()
                    for (document in documents ) {
                        val text = document.get("text") as String
                        val user = document.get("user") as String
                        val chat = Chat(user,text)
                        chats.add(chat)
                        adapter.chats = chats
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}