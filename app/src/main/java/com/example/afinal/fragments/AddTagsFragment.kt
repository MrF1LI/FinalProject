package com.example.afinal.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.afinal.R
import com.example.afinal.databinding.FragmentAddTagsBinding
import java.util.ArrayList


class AddTagsFragment: Fragment(), View.OnClickListener {

    private var _binding: FragmentAddTagsBinding? = null
    private val binding get() = _binding!!

    private lateinit var arrayListTags: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTagsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        arrayListTags = arrayListOf()

        binding.buttonSaveTags.setOnClickListener {
//            val fragment = AddPostFragment()
//            val bundle = Bundle()
//            bundle.putStringArrayList("arrayListTags", arrayListTags)
//            fragment.arguments = bundle

            val controller = Navigation.findNavController(requireView())
            val action = AddTagsFragmentDirections.actionAddTagsFragmentToAddPostFragment(arrayListTags.toTypedArray())
            controller.navigate(action)
        }

        binding.chip.setOnClickListener(this)
        binding.chip1.setOnClickListener(this)
        binding.chip2.setOnClickListener(this)
        binding.chip3.setOnClickListener(this)
        binding.chip4.setOnClickListener(this)
        binding.chip5.setOnClickListener(this)
        binding.chip6.setOnClickListener(this)
        binding.chip7.setOnClickListener(this)
        binding.chip8.setOnClickListener(this)
        binding.chip9.setOnClickListener(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(chip: View?) {
        if (chip is Chip) {
            if (chip.isChecked) {
                arrayListTags.add(chip.text.toString())
                chip.isChecked = true
            } else {
                arrayListTags.remove(chip.text.toString())
                chip.isChecked = false
            }
        }
        Log.e("SHOW", arrayListTags.toString())
    }

}