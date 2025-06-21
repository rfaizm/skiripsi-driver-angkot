package com.example.driverangkot.presentation.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.driverangkot.databinding.LayoutOnlineBinding

class LayoutOnline @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = LayoutOnlineBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

    public fun setOnlineStatus(status: String) {
        binding.textOnline.text = status
    }

}