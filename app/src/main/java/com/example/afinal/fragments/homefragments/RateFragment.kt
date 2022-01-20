package com.example.afinal.fragments.homefragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afinal.R
import com.example.afinal.adapters.RateAdapter
import com.example.afinal.databinding.FragmentRateBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class RateFragment: Fragment (R.layout.fragment_rate), RateAdapter.OnItemClickListener {

    private var _binding: FragmentRateBinding? = null
    private val binding get() = _binding!!

    private val dbLecturers = FirebaseDatabase.getInstance().getReference("lecturers")
    private lateinit var arrayListLecturers: ArrayList<String>
    private lateinit var recyclerViewRates: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        loadRates()
    }

    private fun init() {
        arrayListLecturers = arrayListOf()

        recyclerViewRates = binding.recyclerViewRates

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewRates.layoutManager = layoutManager

        binding.imageViewMore.setOnClickListener {
            showMenu(it, R.menu.menu_rate)
        }

    }

    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.addLecturer) {
                val mainNavController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
                mainNavController.navigate(R.id.action_add_lecturer)
            }
            false
        }

        popup.show()
    }

    private fun loadRates() {
        dbLecturers.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    arrayListLecturers.clear()

                    for (snap in snapshot.children) {
                        val currentLecturer = snap.key.toString()
                        arrayListLecturers.add(currentLecturer)
                    }

                    try {
                        recyclerViewRates.adapter = RateAdapter(context!!.applicationContext, arrayListLecturers, this@RateFragment)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val currentItem = arrayListLecturers[position]

        val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
        val bundle = Bundle()
        bundle.putString("position", currentItem)
        mainNavController.navigate(R.id.action_lecturer_review, bundle)
    }

}