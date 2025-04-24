package com.example.driverangkot.ui.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.driverangkot.databinding.LayoutUploadDocumentBinding

class LayoutUploadDocument@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = LayoutUploadDocumentBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

    fun setTextTitle(text: String) {
        binding.titleDesc.text = text
    }

}