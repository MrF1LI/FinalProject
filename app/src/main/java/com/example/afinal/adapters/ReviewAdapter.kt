package com.example.afinal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.models.Comment
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ReviewAdapter(val context: Context, private val comments: List<Comment>):
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val reviewOwner: TextView = view.findViewById(R.id.reviewOwner)
        val reviewContent: TextView = view.findViewById(R.id.reviewContent)
        val reviewAvatar: CircleImageView = view.findViewById(R.id.reviewAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = comments[position]

        holder.reviewContent.text = currentComment.commentContent
        holder.reviewOwner.text = currentComment.commentOwner

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

        storageReference.child("ProfilePictures/${currentComment.commentOwnerId}").downloadUrl.addOnSuccessListener { url ->
            Glide.with(context).load(url).into(holder.reviewAvatar)
        }.addOnFailureListener {
            try {
                throw it
            } catch (exc: Exception) {
                Log.d("GET_LOG", exc.toString())
            }
        }
    }

    override fun getItemCount() = comments.size

}