package com.example.afinal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.afinal.R
import com.example.afinal.databinding.FragmentAddPostBinding
import com.example.afinal.models.Post
import com.example.afinal.models.Student
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class AddPostFragment: Fragment (R.layout.fragment_add_post) {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private val dbPosts = FirebaseDatabase.getInstance().getReference("posts")
    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val auth = FirebaseAuth.getInstance()

    private var arrayListTags: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        binding.editTextPost.requestFocus()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onResume() {
        super.onResume()

        Log.d("SHOW", "onResume")
        if (arguments != null) {
            arrayListTags = arguments?.getStringArrayList("arrayListTags")!!
            for (tag in arrayListTags) {
                val chip = Chip(this.context)

                chip.text = tag
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    binding.filterTags.removeView(chip)
                }
                binding.filterTags.addView(chip)
            }
        }
    }

    private fun init() {

        binding.editTextPost.requestFocus()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        binding.buttonBack.setOnClickListener {
            val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
            mainNavController.navigate(R.id.action_home)
            childFragmentManager.popBackStack()
        }

        binding.buttonAddPost.setOnClickListener {

            if (binding.editTextPost.text.isNotEmpty()) {

                AlertDialog.Builder(requireContext())
                    .setTitle("Post")
                    .setMessage("Public Post?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        addPost()
                        dialog.dismiss()
                    }.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    .show()

            } else {
                Log.d("Show", "Error")
            }

        }

        binding.buttonAddTags.setOnClickListener {
            val modalBottomSheet = AddTagsBottomSheet()
            modalBottomSheet.show(childFragmentManager, null)

        }

    }

    private fun addPost() {

        dbStudents.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(studentsSnapshot: DataSnapshot) {

                val postOwner = studentsSnapshot.child(auth.currentUser!!.uid).getValue(
                    Student::class.java)?: return
                val postContent = binding.editTextPost.text.toString()
                val postOwnerId = auth.currentUser!!.uid

                val id = dbPosts.push().key.toString()

                dbPosts.child(id).ref.setValue(Post(postContent, postOwner.name + " " + postOwner.surname, postOwnerId, id)).addOnSuccessListener {

                    for (tag in arrayListTags) {
                        dbPosts.child(id).ref.child("postTags").push().setValue(tag)
                    }

                    val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
                    mainNavController.navigate(R.id.action_home)
                    childFragmentManager.popBackStack()
                }

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

}