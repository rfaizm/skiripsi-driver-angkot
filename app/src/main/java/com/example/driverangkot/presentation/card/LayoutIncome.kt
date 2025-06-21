package com.example.driverangkot.presentation.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.driverangkot.databinding.LayoutIncomeBinding

class LayoutIncome @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = LayoutIncomeBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

    fun setSaldo(text: String) {
        binding.textSumSaldo.text = text
    }

}