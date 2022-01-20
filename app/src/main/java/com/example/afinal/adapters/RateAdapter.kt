package com.example.afinal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class RateAdapter(val context: Context, private val lecturers: ArrayList<String>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<RateAdapter.ViewHolder>() {

    private val dbLecturers = FirebaseDatabase.getInstance().getReference("lecturers")

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val lecturerName: TextView = view.findViewById(R.id.lecturerName)
        val lecturerAvatar: CircleImageView = view.findViewById(R.id.lecturerAvatar)
        val lecturerRate: TextView = view.findViewById(R.id.rate)
        val lecturerRating: RatingBar = view.findViewById(R.id.rating)

        private val item: MaterialCardView = view.findViewById(R.id.rateItem)

        init {
            item.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLecturerId = lecturers[position]

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

        storageReference.child("LecturerImages/${currentLecturerId}").downloadUrl.addOnSuccessListener { url ->
            Glide.with(context).load(url).into(holder.lecturerAvatar)
        }.addOnFailureListener {
            try {
                throw it
            } catch (exc: Exception) {
                Log.d("GET_LOG", exc.toString())
            }
        }

        dbLecturers.child(currentLecturerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.lecturerName.text = snapshot.child("lecturerName").getValue(String::class.java)?: return

                snapshot.child("lecturerRates").ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            var result = 0f
                            for (rateSnap in snapshot.children) {
                                val currentRate = rateSnap.value.toString()
                                result += currentRate.toFloat()
                            }
                            val rating = (result / snapshot.childrenCount).toString()
                            holder.lecturerRate.text = String.format("%.1f", rating.toFloat())
                            holder.lecturerRating.rating = rating.toFloat()
                        } else {
                            holder.lecturerRating.rating = 0f
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

    override fun getItemCount() = lecturers.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}