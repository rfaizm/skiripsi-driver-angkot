package com.example.driverangkot.utils

import android.widget.RadioButton
import android.widget.RadioGroup

object RadioButtonUtils {

    fun manageMultipleRadioGroups(vararg radioGroups: RadioGroup) {
        val allRadioButtons = getAllRadioButtons(*radioGroups)

        // Bersihkan semua listener internal
        radioGroups.forEach { it.setOnCheckedChangeListener(null) }

        // Set semua RadioButton bisa mengontrol satu sama lain
        allRadioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                allRadioButtons.forEach { rb -> rb.isChecked = (rb == radioButton) }
            }
        }
    }

    fun setupRadioButtonTextListener(vararg radioGroups: RadioGroup, onRadioSelected: (String) -> Unit) {
        getAllRadioButtons(*radioGroups).forEach { radioButton ->
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) onRadioSelected(buttonView.text.toString())
            }
        }
    }

    fun getAllRadioButtons(vararg radioGroups: RadioGroup): List<RadioButton> =
        radioGroups.flatMap { group ->
            (0 until group.childCount).map { group.getChildAt(it) }.filterIsInstance<RadioButton>()
        }
}
