package com.example.driverangkot

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.databinding.ActivityMainBinding
import com.example.driverangkot.presentation.login.LoginActivity
import com.example.driverangkot.service.LocationUpdateService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(dataStore)

        // Periksa status login sebelum mengatur UI
        checkLoginStatus()

        // Sinkronkan status service hanya jika belum disinkronkan
        if (savedInstanceState == null) {
            syncLocationService()
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_income, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = userPreference.getLogin() ?: false
        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun syncLocationService() {
        val isOnline = userPreference.getStatusOnline() ?: false
        val serviceIntent = Intent(this, LocationUpdateService::class.java)
        if (isOnline && !isServiceRunning(LocationUpdateService::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Log.d("MainActivity", "LocationUpdateService started due to IS_ONLINE=true")
        } else if (!isOnline && isServiceRunning(LocationUpdateService::class.java)) {
            stopService(serviceIntent)
            Log.d("MainActivity", "LocationUpdateService stopped due to IS_ONLINE=false")
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}