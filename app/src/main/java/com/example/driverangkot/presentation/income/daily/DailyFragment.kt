package com.example.driverangkot.presentation.income.daily

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.driverangkot.databinding.FragmentDailyBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.income.IncomeFragment
import com.example.driverangkot.presentation.income.IncomeViewModel
import com.example.driverangkot.presentation.listpassenger.ListPassengerActivity


class DailyFragment : Fragment() {

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<IncomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val TAG = "DailyFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing DailyFragment")
        observeSaldo()
    }

    private fun observeSaldo() {
        viewModel.saldoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    Log.d(TAG, "Loading saldo data")
                    // Tampilkan loading jika diperlukan
                }
                is ResultState.Success -> {
                    Log.d(TAG, "Saldo loaded: daily=${state.data.daily}")
                    binding.textWalletDaily.text = viewModel.formatRupiah(state.data.daily)
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Error loading saldo: ${state.error}")
                    Toast.makeText(requireContext(), "Gagal memuat saldo: ${state.error}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}