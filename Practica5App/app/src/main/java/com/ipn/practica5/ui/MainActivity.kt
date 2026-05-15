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
        binding.toolbar.title = "Hola, ${user?.displayName?.split(" ")?.firstOrNull() ?: "Usuario"} 👋"
        binding.toolbar.setTitleTextColor(0xFFFFFFFF.toInt())

        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        // Verificar conectividad y mostrar banner
        val repo = MediaRepository(this)
        if (!repo.isOnline()) {
            binding.tvOfflineBanner.visibility = View.VISIBLE
        }
    }
}
