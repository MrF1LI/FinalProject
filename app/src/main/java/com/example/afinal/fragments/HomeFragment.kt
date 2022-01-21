package com.example.afinal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.afinal.R
import com.example.afinal.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class HomeFragment: Fragment (R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://students-61271.appspot.com/")

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        showNavigation()
        navigationFunction()
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

        binding.imageUserAvatar.setOnClickListener {
//            val controller = Navigation.findNavController(requireView())
//            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
//            controller.navigate(action)
        }

    }

    private fun showNavigation() {
        binding.buttonBurger.setOnClickListener {
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }
    }

    private fun navigationFunction() {

        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        val controller = nestedNavHostFragment?.navController!!

        val burgerNavigationView = binding.sidebarNavigation

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.communityFragment,
            R.id.chatsFragment,
            R.id.memesFragment,
            R.id.rateFragment
        ))

        burgerNavigationView.setupWithNavController(controller)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


/*button.setOnClickListener {
            val controller = Navigation.findNavController(requireView())
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment()
            controller.navigate(action)
        }*/