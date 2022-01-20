package com.example.afinal

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.afinal.databinding.ActivityRegistrationBinding
import com.example.afinal.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val auth = FirebaseAuth.getInstance()
    private val studentsDb = FirebaseDatabase.getInstance().getReference("students")

    private var imageUri: Uri = Uri.parse("android.resource://com.example.btustudents/drawable/ic_user_default")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        binding.buttonAuthorization.setOnClickListener {
            finish()
        }

        binding.imageViewUserAvatar.setOnClickListener {
            selectImage()
        }

        binding.buttonRegistration.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        saveStudentInfo()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Log.d("Show", "Registration Failed.")
                        Log.d("Show", it.exception.toString())
                    }

                }
        }

    }

    private fun saveStudentInfo() {
        val name = binding.editTextName.text.toString()
        val surname = binding.editTextSurname.text.toString()
        val student = Student(name, surname)

        studentsDb.child(auth.currentUser!!.uid).setValue(student).addOnCompleteListener {
            uploadImage()
        }.addOnFailureListener {
            Log.d("Show", "Failed to save student info")
            Log.d("Show", it.toString())
        }
    }

    private fun uploadImage() {
        val fileName = auth.currentUser!!.uid

        Log.d("GET_LOG", imageUri.toString())

        val storage = FirebaseStorage.getInstance().getReference("ProfilePictures/$fileName")
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

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.imageViewUserAvatar.setImageURI(imageUri)
        }
    }

}