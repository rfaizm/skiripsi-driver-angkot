package com.example.driverangkot.presentation.transfertobank

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.driverangkot.R
import com.example.driverangkot.databinding.ActivityTransferBinding
import com.example.driverangkot.presentation.transfertobank.one.TransferOneFragment
import com.example.driverangkot.presentation.transfertobank.warning.WarningFragment

class TransferActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WarningFragment.newInstance())
                .commitNow()
        }
    }
}