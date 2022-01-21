package com.example.afinal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class MemeAdapter(val context: Context, private val memes: ArrayList<String>):
    RecyclerView.Adapter<MemeAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewMeme: ImageView = view.findViewById(R.id.itemMemes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meme, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = memes[position]
        Log.d("SHOW", "TEST: $currentImage")

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

        storageReference.child("Memes/${currentImage}").downloadUrl.addOnSuccessListener {
            Glide.with(context).load(it).into(holder.imageViewMeme)
        }
    }

    override fun getItemCount() = memes.size

}


/*

.addOnFailureListener {
            try {
                throw it
            } catch (exc: Exception) {
                Log.d("GET_LOG", exc.toString())
            }
        }


 */