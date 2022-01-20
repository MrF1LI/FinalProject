package com.example.afinal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.adapters.MessageAdapter
import com.example.afinal.databinding.FragmentChatBinding
import com.example.afinal.models.Message
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class ChatFragment: Fragment (R.layout.fragment_chat) {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var arrayListMessages: ArrayList<Message>

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("chats")
    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receiverUid = arguments?.getString("uid")
        val senderUid = auth.currentUser!!.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        init()
        loadMessages()
        sendMessage(senderUid)
        info(receiverUid.toString())

    }

    private fun info(receiverUid: String) {

        dbStudents.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentName = snapshot.child(receiverUid).getValue(Student::class.java)?: return

                binding.friendName.text = currentName.name + " " + currentName.surname

                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

                storageReference.child("ProfilePictures/${receiverUid}").downloadUrl.addOnSuccessListener { url ->
                    Glide.with(this@ChatFragment).load(url).into(binding.userAvatar)
                }.addOnFailureListener {
                    try {
                        throw it
                    } catch (exc: Exception) {
                        Log.d("GET_LOG", exc.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun init() {
        recyclerViewMessages = binding.recyclerViewMessages

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = false

        recyclerViewMessages.layoutManager = layoutManager

        arrayListMessages = arrayListOf()

        binding.iconBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadMessages() {
        db.child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                arrayListMessages.clear()

                for (messageSnapshot in snapshot.children) {
                    val currentMessage = messageSnapshot.getValue(Message::class.java)?: return
                    arrayListMessages.add(currentMessage)
                }

                recyclerViewMessages.adapter = MessageAdapter(requireContext(), arrayListMessages)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendMessage(senderUid: String) {

        binding.iconSendMessage.setOnClickListener {
            val messageText = binding.editTextMessage.text.toString()
            val message = Message(messageText, senderUid)

            db.child(senderRoom!!).child("messages").push().setValue(message).addOnSuccessListener {
                db.child(receiverRoom!!).child("messages").push().setValue(message)
            }.addOnSuccessListener {

                dbStudents.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.child(auth.currentUser!!.uid).getValue(Student::class.java)?: return
                        val currentUserName = currentUser.name + " " + currentUser.surname
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }

            recyclerViewMessages.smoothScrollToPosition(arrayListMessages.size)

            binding.editTextMessage.text.clear()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}