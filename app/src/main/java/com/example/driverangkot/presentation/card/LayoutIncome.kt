package com.example.driverangkot.presentation.card

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.driverangkot.databinding.LayoutIncomeBinding
import com.example.driverangkot.presentation.transfertobank.TransferActivity

class LayoutIncome @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = LayoutIncomeBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.imageGoToBank.setOnClickListener {
            goToBank()
        }
    }

    fun goToBank() {
        context.startActivity(Intent(context, TransferActivity::class.java))
    }
    fun setSaldo(text: String) {
        binding.textSumSaldo.text = text
    }

}