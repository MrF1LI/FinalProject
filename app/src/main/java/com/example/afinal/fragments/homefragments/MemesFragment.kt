package com.example.afinal.fragments.homefragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.afinal.R
import com.example.afinal.adapters.MemeAdapter
import com.example.afinal.databinding.FragmentMemesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class MemesFragment: Fragment(R.layout.fragment_memes) {

    private var _binding: FragmentMemesBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val dbMemes = FirebaseDatabase.getInstance().getReference("memes")

    private lateinit var imageUri: Uri

    private lateinit var arrayListMemes: ArrayList<String>
    private lateinit var recyclerViewMemes: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadMemes()
    }

    private fun init() {
        arrayListMemes = arrayListOf()
        recyclerViewMemes = binding.recyclerViewMemes

        val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)

        recyclerViewMemes.layoutManager = layoutManager

        binding.addMeme.setOnClickListener {
            selectImage()
        }
    }

    private fun loadMemes() {
        dbMemes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    arrayListMemes.clear()

                    for (snap in snapshot.children) {
                        val currentMeme = snap.key.toString()
                        Log.d("SHOW", "KEY: $currentMeme")
                        arrayListMemes.add(currentMeme)
                    }

                    try {
                        Log.d("SHOW", arrayListMemes.toString())
                        recyclerViewMemes.adapter = MemeAdapter(context!!.applicationContext, arrayListMemes)
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

    private fun uploadImage() {

        val id = dbMemes.push().key

        val storage = FirebaseStorage.getInstance().getReference("Memes/$id")
        storage.putFile(imageUri).addOnSuccessListener {
            dbMemes.child(id.toString()).setValue(auth.currentUser!!.uid)
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

            AlertDialog.Builder(requireContext())
                .setTitle("Meme")
                .setMessage("Public Meme?")
                .setPositiveButton("Yes") { dialog, _ ->
                    uploadImage()
                    dialog.dismiss()
                }.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}