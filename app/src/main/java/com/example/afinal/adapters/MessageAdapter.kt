package com.example.afinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.models.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, private val messages: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    private val itemReceive = 1
    private val itemSent = 2

    class SentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sentMessage: TextView = view.findViewById(R.id.textViewSentMessage)
    }

    class ReceiveViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val receivedMessage: TextView = view.findViewById(R.id.textViewReceiveMessage)
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messages[position]

        return if (auth.currentUser!!.uid == currentMessage.senderId) {
            itemSent
        } else {
            itemReceive
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == itemReceive) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_receive, parent, false)
            ReceiveViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_sent, parent, false)
            SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messages[position]

        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder =  holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            holder.receivedMessage.text = currentMessage.message
        }

    }

    override fun getItemCount() = messages.size
}