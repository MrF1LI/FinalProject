package com.example.afinal.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.afinal.AuthenticationActivity
import com.example.afinal.R
import com.example.afinal.databinding.FragmentHomeBinding
import com.example.afinal.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment: Fragment (R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("students")
    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        storageReference.child("ProfilePictures/${auth.currentUser!!.uid}").downloadUrl.addOnSuccessListener { url ->
            Glide.with(this).load(url).into(binding.imageUserAvatar)
        }.addOnFailureListener {
            try {
                throw it
            } catch (exc: Exception) {
                Log.d("GET_LOG", exc.toString())
            }
        }

        db.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val surname = snapshot.child("surname").value.toString()
                binding.userName.text = "$name $surname"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.buttonLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, AuthenticationActivity::class.java))
            activity?.finish()
        }

        binding.buttonBack.setOnClickListener {
            val mainNavController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
            mainNavController.navigate(R.id.action_home)
        }

        binding.changePassword.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.updatePassword(binding.editTextNewPassword.text.toString())
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MaterialAlertDialogBuilder(
                            requireContext(),
                            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
                            .setMessage("Password changed successfully.")
                            .setTitle("Change Password")
                            .setPositiveButton("Ok") { dialog, which ->

                            }
                            .show()
                    } else {
                        Toast.makeText(activity, "Password change failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}