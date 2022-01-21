package com.example.afinal.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.afinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.bumptech.glide.Glide
import com.example.afinal.models.Post
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception


class PostsAdapter(val context: Context, private val posts: List<Post>, private val listener: OnItemClickListener): RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private val dbPosts = FirebaseDatabase.getInstance().getReference("posts")
    private val auth = FirebaseAuth.getInstance()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val postOwner: TextView = view.findViewById(R.id.postOwner)
        val postOwnerAvatar: CircleImageView = view.findViewById(R.id.userAvatar)
        val postContent: TextView = view.findViewById(R.id.postContent)
        val commentCount: TextView = view.findViewById(R.id.commentCount)
        val reactCount: TextView = view.findViewById(R.id.reactCount)

        val recyclerViewTag: RecyclerView = view.findViewById(R.id.recyclerViewTags) // new

        private val item: RelativeLayout = view.findViewById(R.id.post)

        init {
            item.setOnClickListener(this)

            item.findViewById<ImageView>(R.id.iconReact).setOnClickListener {
                listener.onReactClick(position, item.findViewById(R.id.iconReact))
            }

            recyclerViewTag.addItemDecoration(
                SpacingItemDecoration(
                    context.resources.getDimensionPixelOffset(R.dimen.item_space),
                    context.resources.getDimensionPixelOffset(R.dimen.item_space)
                )
            )
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentPost = posts[position]

        holder.postOwner.text = currentPost.postOwner
        holder.postContent.text = currentPost.postContent

        Log.d("SHOW", "TESTPOST")
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

        storageReference.child("ProfilePictures/${currentPost.postOwnerId}").downloadUrl.addOnSuccessListener { url ->
            Glide.with(context).load(url).into(holder.postOwnerAvatar)
        }.addOnFailureListener {
            try {
                throw it
            } catch (exc: Exception) {
                Log.d("GET_LOG", exc.toString())
            }
        }

        dbPosts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val arrayListTags: ArrayList<String> = arrayListOf()
                val idd = posts[position]
                val current = snapshot.child(idd.postId.toString())

                for (tag in current.child("postTags").children) {
                    val currentTag = tag.getValue(String::class.java)?: return
                    arrayListTags.add(currentTag)
                }

                nestedRecycler(holder.recyclerViewTag, arrayListTags)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //

        dbPosts.child(posts[position].postId.toString()).child("postComments").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val commentsCount = snapshot.childrenCount
                Log.e("VNAXOT", "COMMENT:$commentsCount")
                holder.commentCount.text = "$commentsCount Comment"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbPosts.child(posts[position].postId.toString()).child("postReacts").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val reactCount = snapshot.childrenCount
                Log.e("VNAXOT", "REACT:$reactCount")
                holder.reactCount.text = "$reactCount Like"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val arrayList = arrayListOf<String>()

        dbPosts.child(posts[position].postId.toString()).child("postReacts").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val id = snap.getValue(String::class.java)?: return
                    arrayList.add(id)
                }

                if (arrayList.contains(auth.currentUser!!.uid)) {
                    holder.itemView.findViewById<ImageView>(R.id.iconReact).setImageResource(R.drawable.ic_reacted)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun getItemCount() = posts.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onReactClick(position: Int, view: ImageView)
    }

    private fun nestedRecycler(recyclerView: RecyclerView, tagList: ArrayList<String>) {

        val layoutManager = ChipsLayoutManager.newBuilder(context)
            .build()

        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = TagAdapter(context, tagList)
    }

}