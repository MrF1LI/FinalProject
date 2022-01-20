package com.example.afinal.fragments.homefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.databinding.FragmentChatsBinding
import com.example.afinal.adapters.ChatsAdapter
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.oAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsFragment: Fragment(R.layout.fragment_chats), ChatsAdapter.OnItemClickListener {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var arrayListFriends: ArrayList<String>
    private lateinit var recyclerViewChats: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadChats()
    }

    private fun init() {
        arrayListFriends = arrayListOf()
        recyclerViewChats = binding.recyclerViewChats

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewChats.layoutManager = layoutManager
    }

    private fun loadChats() {
        dbStudents.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                arrayListFriends.clear()

                for (friendSnapshot in snapshot.children) {

                    val currentFriend = friendSnapshot.key.toString()

                    if (currentFriend != auth.currentUser!!.uid) {
                        arrayListFriends.add(currentFriend)
                    }

                }

                recyclerViewChats.adapter = ChatsAdapter(context!!.applicationContext, arrayListFriends, this@ChatsFragment)
                Log.d("Show", arrayListFriends.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {

        Log.d("TEST_LOG", "onItemClick")

        dbStudents.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bundle = Bundle()
                val currentStudent = arrayListFriends[position]

                Log.d("TEST_LOG", "dbStudents")

                val student = snapshot.child(currentStudent).getValue(Student::class.java)?: return
                bundle.putString("name", student.name + " " + student.surname)
                bundle.putString("uid", currentStudent)
                val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
                mainNavController.navigate(R.id.action_chat, bundle)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}