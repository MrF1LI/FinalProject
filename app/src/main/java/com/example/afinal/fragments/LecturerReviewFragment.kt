package com.example.afinal.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.adapters.ReviewAdapter
import com.example.afinal.databinding.FragmentLecturerReviewBinding
import com.example.afinal.models.Comment
import com.example.afinal.models.Student
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.lang.NullPointerException

class LecturerReviewFragment: Fragment (R.layout.fragment_lecturer_review) {

    private var _binding: FragmentLecturerReviewBinding? = null
    private val binding get() = _binding!!

    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val dbLecturers = FirebaseDatabase.getInstance().getReference("lecturers")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var arrayListReviews: ArrayList<Comment>
    private lateinit var recyclerViewReviews: RecyclerView

    private var currentReviewId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLecturerReviewBinding.inflate(inflater, container, false)

        currentReviewId = requireArguments().getString("position").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadCurrentLecturerReview(currentReviewId)
    }

    private fun init() {

        arrayListReviews = arrayListOf()

        recyclerViewReviews = binding.recyclerViewReviews

        val layoutManager = LinearLayoutManager(requireContext())

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewReviews.layoutManager = layoutManager

        binding.buttonAddReview.setOnClickListener {
            if (binding.editTextReview.text.isNotEmpty()) {
                addNewReview(currentReviewId)
            }
        }

    }

    private fun loadCurrentLecturerReview(currentReviewId: String) {

        dbLecturers.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentLecturer = snapshot.child(currentReviewId)
                binding.lecturerName.text = currentLecturer.child("lecturerName").value.toString()

                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

                storageReference.child("LecturerImages/$currentLecturer").downloadUrl.addOnSuccessListener { url ->
                    Glide.with(this@LecturerReviewFragment).load(url).into(binding.lecturerAvatar)
                }.addOnFailureListener {
                    try {
                        throw it
                    } catch (exc: Exception) {
                        Log.d("GET_LOG", exc.toString())
                    }
                }

                currentLecturer.child("lecturerRates").ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var result = 0f
                            for (snap in snapshot.children) {
                                val currentRate = snap.value.toString()
                                result += currentRate.toFloat()
                            }
                            binding.rating.rating = result
                            binding.rate.text = result.toString()
                        } else {
                            binding.rating.rating = 0f
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                currentLecturer.child("lecturerReviews").ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        arrayListReviews.clear()

                        binding.lecturerReviews.text = "${snapshot.childrenCount} Review"

                        for (snap in snapshot.children) {
                            val currentReview = snap.getValue(Comment::class.java)?: return
                            arrayListReviews.add(currentReview)
                        }

                        try {
                            recyclerViewReviews.adapter = ReviewAdapter(context!!.applicationContext, arrayListReviews)
                        } catch (e: Exception) {
                            Log.e("SHOW", "Arvimchnevt")
                            Log.e("SHOW", e.message.toString())
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addNewReview(currentReviewId: String) {

        val dialog: DialogFragment = RateDialog()

        val bundle = Bundle()
        bundle.putString("currentReviewId", currentReviewId)
        dialog.show(childFragmentManager, null)

        dialog.arguments = bundle

        dbStudents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentStudent = snapshot.child(auth.currentUser!!.uid).getValue(Student::class.java)?: return
                val newReviewContent = binding.editTextReview.text.toString()
                val newReviewOwner = currentStudent.name + " " + currentStudent.surname

                val newReview = Comment(newReviewContent, newReviewOwner, auth.currentUser!!.uid)

                dbLecturers.child(currentReviewId).child("lecturerReviews").push().setValue(newReview).addOnSuccessListener {
                    binding.editTextReview.text.clear()
                    binding.editTextReview.clearFocus()
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