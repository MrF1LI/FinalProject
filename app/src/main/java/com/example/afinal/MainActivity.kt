package com.example.afinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {

        /* navigation between fragments */
        val controller = findNavController(R.id.nav_host_fragment_container)

        val appBarConfig = AppBarConfiguration(setOf(
            R.id.nav_graph_home,
            R.id.chatFragment,
            R.id.postInfoFragment,
            R.id.profileFragment,
            R.id.addPostFragment
        ))

        setupActionBarWithNavController(controller, appBarConfig)

        /*  */

    }

}