package com.example.driverangkot.presentation.listpassenger

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.driverangkot.R
import com.example.driverangkot.databinding.ActivityListPassengerBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.adapter.ListPassengerPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class ListPassengerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListPassengerBinding
    private val viewModel: ListPassengersViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val TAG = "ListPassengerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPassengerBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        goToHome()
        setupViewPager()
        observeUpdateStatus()
        viewModel.fetchPassengers() // [Baru] Panggil fetchPassengers saat inisialisasi
    }

    private fun setupViewPager() {
        val adapter = ListPassengerPagerAdapter(this)
        binding.viewPagerList.adapter = adapter

        TabLayoutMediator(binding.tabLayoutList, binding.viewPagerList) { tab, position ->
            tab.text = when (position) {
                0 -> "Menuju Tujuan"
                1 -> "Menunggu Dijemput"
                else -> ""
            }
        }.attach()

        // [Baru] Listener untuk refresh data saat pindah tab
        binding.viewPagerList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "Tab selected: position=$position")
                viewModel.fetchPassengers()
            }
        })
    }

    private fun goToHome() {
        binding.closeImage.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeUpdateStatus() {
        viewModel.updateStatusState.observe(this) { state ->
            when (state) {
                is ResultState.Success -> {
                    Log.d(TAG, "Update status success, switching to PickUpFragment if status is dijemput")
                    if (binding.viewPagerList.currentItem == 1) { // Jika di tab WaitingFragment
                        binding.viewPagerList.setCurrentItem(0, true) // Pindah ke PickUpFragment
                    }
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Update status error: ${state.error}")
                }
                is ResultState.Loading -> {
                    Log.d(TAG, "Updating status, showing loading")
                }
            }
        }
    }
}