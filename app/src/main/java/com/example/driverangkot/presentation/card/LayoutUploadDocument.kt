package com.example.driverangkot.presentation.card

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.driverangkot.databinding.LayoutUploadDocumentBinding

class LayoutUploadDocument @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    private val binding = LayoutUploadDocumentBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // Pastikan CardView dapat diklik
        isClickable = true
        isFocusable = true
    }

    fun setTextTitle(text: String) {
        binding.titleDesc.text = text
    }

    fun setImageUri(uri: Uri?) {
        uri?.let {
            binding.imagePreview.setImageURI(it)
        }
    }
}