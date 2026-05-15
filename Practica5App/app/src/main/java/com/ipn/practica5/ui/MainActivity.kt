package com.ipn.practica5.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.ipn.practica5.databinding.ActivityMainBinding
import com.ipn.practica5.repository.MediaRepository

/**
 * Activity principal que aloja el bottom navigation y los fragments.
 * Muestra el banner de modo offline cuando no hay conexión a internet.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val user = FirebaseAuth.getInstance().currentUser
        val firstName = user?.displayName?.split(" ")?.firstOrNull() ?: "Usuario"
        supportActionBar?.title = "Hola, $firstName 👋"
        binding.toolbar.setTitleTextColor(android.graphics.Color.WHITE)

        val navHost = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        // Banner offline
        if (!MediaRepository(this).isOnline()) {
            binding.tvOfflineBanner.visibility = View.VISIBLE
        }
    }
}
