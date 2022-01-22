package com.example.afinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.afinal.databinding.ActivityResetPasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        binding.buttonResetPassword.setOnClickListener {

            val email = binding.editTextEmail.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        MaterialAlertDialogBuilder(this,
                            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
                            .setMessage("Check email to reset your password.")
                            .setTitle("Reset password")
                            .setPositiveButton("Ok") { dialog, which ->
                                // Respond to positive button press
                            }
                            .show()
                    } else {
                        MaterialAlertDialogBuilder(this,
                            R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
                            .setMessage("Check email to reset your password.")
                            .setTitle("Reset password")
                            .setPositiveButton("Ok") { dialog, which ->
                                // Respond to positive button press
                            }
                            .show()
                    }
                }

        }

        binding.buttonBack.setOnClickListener {
            finish()
        }


    }

}