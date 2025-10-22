package com.example.driverangkot.presentation.transfertobank.one

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.driverangkot.R
import com.example.driverangkot.databinding.FragmentTransferoneBinding
import com.example.driverangkot.utils.RadioButtonUtils
import com.example.driverangkot.utils.Utils


class TransferOneFragment : Fragment() {

    private var _binding : FragmentTransferoneBinding? = null

    private val binding get() = _binding!!

    private var isUpdatingFromRadio = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioButtons()
        setupInputTextListener()
        setupButtonConfirmation()
    }

    private fun setupRadioButtons() {
        val rightGroup = binding.radioGroupTopupRight
        val leftGroup = binding.radioGroupTopupLeft

        RadioButtonUtils.manageMultipleRadioGroups(rightGroup, leftGroup)

        RadioButtonUtils.setupRadioButtonTextListener(rightGroup, leftGroup) { selectedText ->
            isUpdatingFromRadio = true
            // Mengubah format "Rp. 50.000" menjadi "50000"
            val cleanedText = selectedText
                .replace("Rp. ", "")
                .replace(".", "")
            binding.inputTopupInputText.setText(cleanedText)
            binding.buttonConfirmation.isEnabled = true // Aktifkan tombol saat radio dipilih
            isUpdatingFromRadio = false
        }
    }

    private fun setupInputTextListener() {
        binding.inputTopupInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingFromRadio) {
                    // Reset radio buttons jika input diubah manual
                    RadioButtonUtils.getAllRadioButtons(
                        binding.radioGroupTopupRight,
                        binding.radioGroupTopupLeft
                    ).forEach { it.isChecked = false }
                }
                // Aktifkan/nonaktifkan tombol berdasarkan input
                binding.buttonConfirmation.isEnabled = !s.isNullOrEmpty() && s.toString().trim().isNotBlank()
            }
        })
    }

    private fun setupButtonConfirmation() {
        // Nonaktifkan tombol secara default
        binding.buttonConfirmation.isEnabled = false

        binding.buttonConfirmation.setOnClickListener {
            val inputText = binding.inputTopupInputText.text.toString().trim()
            val nominal = inputText.toIntOrNull()

            if (nominal == null || nominal <= 0) {
                Toast.makeText(requireContext(), "Masukkan nominal top-up yang valid", Toast.LENGTH_SHORT).show()
                Log.e("TopUpOneFragment", "Nominal tidak valid: $inputText")
                return@setOnClickListener
            }

            // Panggil dialog konfirmasi
            Utils.showConfirmationDialog(
                context = requireContext(),
                navigateToNextFragment = { Log.d("TransferOneFragment", "Ini hasil ${nominal}") },
                textTitle = "Konfirmasi Top Up",
                textMessage = "Apakah Anda yakin ingin melanjutkan top up sebesar ${Utils.formatNumber(nominal)}?",
                textPositive = "Ya",
                textNegative = "Tidak"
            )
        }
    }

    companion object {
        fun newInstance() = TransferOneFragment()
    }
}