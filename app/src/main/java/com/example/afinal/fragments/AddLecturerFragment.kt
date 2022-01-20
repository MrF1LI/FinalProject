package com.example.afinal.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.afinal.R
import com.example.afinal.databinding.FragmentAddLecturerBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddLecturerFragment: Fragment(R.layout.fragment_add_lecturer) {

    private var _binding: FragmentAddLecturerBinding? = null
    private val binding get() = _binding!!

    private val dbLecturer = FirebaseDatabase.getInstance().getReference("lecturers")
    private var imageUri: Uri = Uri.parse("android.resource://com.example.btustudents/drawable/ic_user_default")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLecturerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.buttonAddLecturer.setOnClickListener {
            if (binding.editTextLecturerName.text.isNotEmpty()) {
                addLecturer()
            }
        }

        binding.imageViewLecturer.setOnClickListener {
            selectImage()
        }
    }

    private fun addLecturer() {
        val name = binding.editTextLecturerName.text.toString()
        val id = dbLecturer.push().key
        dbLecturer.child(id.toString()).child("lecturerName").setValue(name).addOnSuccessListener {
            uploadImage(id.toString())
        }
    }

    private fun uploadImage(id: String) {
        val storage = FirebaseStorage.getInstance().getReference("LecturerImages/$id")
        storage.putFile(imageUri).addOnSuccessListener {
            Log.d("MY_TAG", "Success")
        }.addOnFailureListener {
            Log.d("MY_TAG", "Failed")
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data!!
            binding.imageViewLecturer.setImageURI(imageUri)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}