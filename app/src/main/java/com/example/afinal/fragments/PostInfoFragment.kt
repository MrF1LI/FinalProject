package com.example.afinal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.adapters.CommentsAdapter
import com.example.afinal.adapters.TagAdapter
import com.example.afinal.databinding.FragmentPostInfoBinding
import com.example.afinal.models.Comment
import com.example.afinal.models.Post
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class PostInfoFragment: Fragment (R.layout.fragment_post_info) {

    private var _binding: FragmentPostInfoBinding? = null
    private val binding get() = _binding!!

    private val dbPosts = FirebaseDatabase.getInstance().getReference("posts")
    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var arrayListComments: ArrayList<Comment>
    private lateinit var recyclerViewComments: RecyclerView

    private lateinit var arrayListTags: ArrayList<String>
    private lateinit var recyclerViewTags: RecyclerView

    private var currentPostId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostInfoBinding.inflate(inflater, container, false)

        currentPostId = requireArguments().getString("position").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadCurrentPost(currentPostId)
        loadPostComments(currentPostId)
        checkReact(currentPostId)
        addReact(currentPostId)
    }

    private fun init() {

        arrayListComments = arrayListOf()
        recyclerViewComments = binding.recyclerViewComments

        val layoutManager = LinearLayoutManager(requireContext())

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewComments.layoutManager = layoutManager

        //


        arrayListTags = arrayListOf()
        recyclerViewTags = binding.recyclerViewTags

        val layoutManagerTags = ChipsLayoutManager.newBuilder(activity)
            .build()

        recyclerViewTags.layoutManager = layoutManagerTags
        recyclerViewTags.addItemDecoration(
            SpacingItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.item_space),
                resources.getDimensionPixelOffset(R.dimen.item_space)
            )
        )

        //

        binding.buttonAddComment.setOnClickListener {
            if (binding.editTextNewComment.text.isNotEmpty()) {
                addNewComment(currentPostId)
            }
        }

    }

    private fun loadCurrentPost(currentPostId: String) {

        dbPosts.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val current = snapshot.child(currentPostId)

                    for (tag in current.child("postTags").children) {
                        val currentTag = tag.getValue(String::class.java)?: return
                        arrayListTags.add(currentTag)
                    }

                    recyclerViewTags.adapter = TagAdapter(requireContext(), arrayListTags)

                    val currentPost = current.getValue(Post::class.java)?: return

                    binding.postOwner.text = currentPost.postOwner
                    binding.postContent.text = currentPost.postContent

                    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

                    storageReference.child("ProfilePictures/${currentPost.postOwnerId}").downloadUrl.addOnSuccessListener { url ->
                        Glide.with(this@PostInfoFragment).load(url).into(binding.userAvatar)
                    }.addOnFailureListener {
                        try {
                            throw it
                        } catch (exc: Exception) {
                            Log.d("GET_LOG", exc.toString())
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbPosts.child(currentPostId).child("postReacts").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val count = snapshot.childrenCount
                    binding.reactCount.text = "$count Like"
                } catch (e: Exception) {
                    Log.e("SHOW", "Arvimchnevt")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun loadPostComments(currentPostId: String) {
        val currentPost = currentPostId
        Log.d("Show", currentPostId)

        dbPosts.child(currentPost).child("postComments").addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    /* count */

                    try {
                        val count = snapshot.childrenCount
                        binding.commentCount.text = "$count Comment"
                    } catch (e: Exception) {
                        Log.e("SHOW", "Arvinchnevt")
                    }

                    /* load */

                    arrayListComments.clear()

                    for (commentSnapshot in snapshot.children) {
                        val currentComment = commentSnapshot.getValue(Comment::class.java)?: return
                        arrayListComments.add(currentComment)
                    }

                    try {
                        recyclerViewComments.adapter = CommentsAdapter(requireContext(), arrayListComments)
                    } catch (e: Exception) {
                        Log.e("SHOW", "Arvimchnevt")
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun addNewComment(currentPostId: String) {
        dbStudents.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentStudent = snapshot.child(auth.currentUser!!.uid).getValue(Student::class.java)?: return
                val newCommentContent = binding.editTextNewComment.text.toString()
                val newCommentOwner = currentStudent.name + " " + currentStudent.surname

                val newComment = Comment(newCommentContent, newCommentOwner, auth.currentUser!!.uid)

                dbPosts.child(currentPostId).child("postComments").ref.push().setValue(newComment).addOnSuccessListener {
                    binding.editTextNewComment.text.clear()
                    binding.editTextNewComment.clearFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun checkReact(currentPostId: String) {
        val arrayList = arrayListOf<String>()

        dbPosts.child(currentPostId).child("postReacts").addListenerForSingleValueEvent(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val id = snap.getValue(String::class.java)?: return
                    arrayList.add(id)
                }

                if (arrayList.contains(auth.currentUser!!.uid)) {
                    binding.iconReact.setImageResource(R.drawable.ic_reacted)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addReact(currentPostId: String) {

        binding.iconReact.setOnClickListener {
            val arrayList = arrayListOf<String>()

            dbPosts.child(currentPostId).child("postReacts").addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        val id = snap.getValue(String::class.java)?: return
                        arrayList.add(id)
                    }

                    if (!arrayList.contains(auth.currentUser!!.uid)) {
                        snapshot.ref.push().setValue(auth.currentUser!!.uid).addOnSuccessListener {
                            binding.iconReact.setImageResource(R.drawable.ic_reacted)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {

            false
        }

        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}