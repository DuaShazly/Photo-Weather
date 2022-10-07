package com.robusta.photoweather.ui.activity

import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.robusta.photoweather.R.id
import com.robusta.photoweather.R.navigation
import com.robusta.photoweather.databinding.ActivityMainBinding
import com.robusta.photoweather.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentNavController: LiveData<NavController>? = null

    @RequiresApi(VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        setupBottomNavigationBar()
    }

    @RequiresApi(VERSION_CODES.S)
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(id.bottom_navigation_view)

        bottomNavigationView.itemIconTintList = null

        val navGraphIds = listOf(
            navigation.capture_nav,
            navigation.history_nav
        )

        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = id.nav_host_container,
            intent = intent
        )

        controller.observe(this, Observer { navController ->
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }


}