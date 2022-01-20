package com.example.afinal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.afinal.R
import com.example.afinal.databinding.DialogRateBinding
import com.example.afinal.models.Comment
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RateDialog: DialogFragment(R.layout.dialog_rate) {

    private val dbLecturers = FirebaseDatabase.getInstance().getReference("lecturers")
    private val auth = FirebaseAuth.getInstance()
    private var _binding: DialogRateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentReviewId = arguments?.getString("currentReviewId")

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonRate.setOnClickListener {
            dbLecturers.child(currentReviewId.toString()).child("lecturerRates")
                .child(auth.currentUser!!.uid).setValue(binding.rateLecturer.rating)

            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}