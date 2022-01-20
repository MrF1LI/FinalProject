package com.example.afinal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.MainActivity
import com.example.afinal.R
import com.example.afinal.fragments.HomeFragment
import com.example.afinal.models.Message
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ChatsAdapter(val context: Context, private val chats: List<String>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val dbChats = FirebaseDatabase.getInstance().getReference("chats")
    private val auth = FirebaseAuth.getInstance()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val chatName: TextView = view.findViewById(R.id.chatName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val chatAvatar: CircleImageView = view.findViewById(R.id.chatUserAvatar)

        private val item: LinearLayout = view.findViewById(R.id.itemChats)

        init {
            item.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentChat = chats[position]

        dbStudents.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentStudent = snapshot.child(currentChat).getValue(Student::class.java)?: return

                holder.chatName.text = currentStudent.name + " " + currentStudent.surname

                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

                storageReference.child("ProfilePictures/${currentChat}").downloadUrl.addOnSuccessListener { url ->
                    Glide.with(context).load(url).into(holder.chatAvatar)
                }.addOnFailureListener {
                    try {
                        throw it
                    } catch (exc: Exception) {
                        Log.d("GET_LOG", exc.toString())
                    }
                }

                val chatRoom = auth.currentUser!!.uid + currentChat
                Log.d("Show", chatRoom)
                dbChats.child(chatRoom).child("messages").limitToLast(1).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (snap in snapshot.children) {
                            val lastMessage = snap.getValue(Message::class.java)?: return
                            holder.lastMessage.text = lastMessage.message
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                Log.d("Show", "test: $currentStudent")

//                holder.itemView.setOnClickListener {
//                    val mainNavController = Navigation.findNavController(MainActivity(), R.id.nav_host_fragment_container)
//                    val bundle = Bundle()
//                    bundle.putString("name", currentStudent.name + " " + currentStudent.surname)
//                    bundle.putString("uid", snapshot.child(currentChat).key)
//                    mainNavController.navigate(R.id.action_chat, bundle)
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getItemCount() = chats.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}