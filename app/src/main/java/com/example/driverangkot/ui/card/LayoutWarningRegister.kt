package com.example.driverangkot.ui.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.driverangkot.databinding.LayoutWarningRegisterBinding

class LayoutWarningRegister@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = LayoutWarningRegisterBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // Default:
    }
}