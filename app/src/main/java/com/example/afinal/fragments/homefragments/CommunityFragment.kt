package com.example.afinal.fragments.homefragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.adapters.PostsAdapter
import com.example.afinal.databinding.FragmentCommunityBinding
import com.example.afinal.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class CommunityFragment: Fragment(R.layout.fragment_community), PostsAdapter.OnItemClickListener {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private val postsDb = FirebaseDatabase.getInstance().getReference("posts")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var arrayListPosts: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        Log.d("VNAXOT", "onViewCreated")
        loadPosts()

    }

    private fun init() {

        recyclerViewPosts = binding.recyclerViewPosts

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewPosts.layoutManager = layoutManager

        arrayListPosts = arrayListOf()

        binding.buttonAddPost.setOnClickListener {
            val mainNavController = findNavController(requireActivity(), R.id.nav_host_fragment_container)
            mainNavController.navigate(R.id.action_add_post)
        }

    }



    private fun loadPosts() {

        postsDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                arrayListPosts.clear()

                for (postSnapshot in snapshot.children) {

                    val post = postSnapshot.getValue(Post::class.java)?: return
                    arrayListPosts.add(post)
                }

                try {
                    recyclerViewPosts.adapter = PostsAdapter(requireContext(), arrayListPosts, this@CommunityFragment)
                } catch (e: Exception) {
                    Log.e("SHOW", "Arvinmchnevt")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val mainNavController = findNavController(requireActivity(), R.id.nav_host_fragment_container)

        val currentItem = arrayListPosts[position]

        val bundle = Bundle()
        bundle.putString("position", currentItem.postId.toString())
        mainNavController.navigate(R.id.action_post_info, bundle)
    }

    override fun onReactClick(position: Int, view: ImageView) {

        val arrayList = arrayListOf<String>()

        val currentItem = arrayListPosts[position]

        postsDb.child(currentItem.postId.toString()).child("postReacts").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val id = snap.getValue(String::class.java)?: return
                    arrayList.add(id)
                }

                if (!arrayList.contains(auth.currentUser!!.uid)) {
                    snapshot.ref.push().setValue(auth.currentUser!!.uid).addOnSuccessListener {
                        view.setImageResource(R.drawable.ic_reacted)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



}