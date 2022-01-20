package com.example.afinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.google.android.material.chip.Chip

class TagAdapter(val context: Context, private val tags: ArrayList<String>):
    RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tag: Chip = view.findViewById(R.id.tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTag = tags[position]
        holder.tag.text = currentTag


        val icon: Int = when (currentTag) {
            "IT" -> R.drawable.ic_laptop
            "მენეჯმენტი" -> R.drawable.ic_business
            "ფინანსები" -> R.drawable.ic_finances
            "I კურსი", "II კურსი", "III კურსი", "IV, კურსი" -> R.drawable.ic_course
            "გამოცდა" -> R.drawable.ic_exam
            else -> R.drawable.ic_tag
        }

        holder.tag.setChipIconResource(icon)

    }

    override fun getItemCount() = tags.size

}