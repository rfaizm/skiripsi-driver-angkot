package com.example.driverangkot.utils

import android.widget.RadioButton
import android.widget.RadioGroup
import kotlin.collections.filterIsInstance
import kotlin.collections.firstOrNull
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.ranges.until

class RadioButtonUtils private constructor() {

    companion object {
        fun manageMultipleRadioGroups(vararg radioGroups: RadioGroup) {
            val allRadioButtons = getAllRadioButtons(*radioGroups)

            // Bersihkan semua listener internal
            radioGroups.forEach { it.setOnCheckedChangeListener(null) }

            // Set semua RadioButton bisa mengontrol satu sama lain
            allRadioButtons.forEach { radioButton ->
                radioButton.setOnClickListener {
                    allRadioButtons.forEach { rb ->
                        rb.isChecked = (rb == radioButton)
                    }
                }
            }
        }


        fun getSelectedRadioButton(vararg radioGroups: RadioGroup): RadioButton? {
            return radioGroups.flatMap { group ->
                (0 until group.childCount)
                    .map { group.getChildAt(it) }
                    .filterIsInstance<RadioButton>()
            }.firstOrNull { it.isChecked }
        }

        fun setupRadioButtonTextListener(
            vararg radioGroups: RadioGroup,
            onRadioSelected: (String) -> Unit
        ) {
            val allRadioButtons = getAllRadioButtons(*radioGroups)

            allRadioButtons.forEach { radioButton ->
                radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        onRadioSelected(buttonView.text.toString())
                    }
                }
            }
        }

        fun getAllRadioButtons(vararg radioGroups: RadioGroup): List<RadioButton> {
            return radioGroups.flatMap { group ->
                (0 until group.childCount)
                    .map { group.getChildAt(it) }
                    .filterIsInstance<RadioButton>()
            }
        }
    }
}