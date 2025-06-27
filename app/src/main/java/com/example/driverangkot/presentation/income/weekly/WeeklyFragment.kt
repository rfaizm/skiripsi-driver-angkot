package com.example.driverangkot.presentation.income.weekly

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.driverangkot.databinding.FragmentWeeklyBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.income.IncomeViewModel


class WeeklyFragment : Fragment() {

    private var _binding: FragmentWeeklyBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<IncomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val TAG = "WeeklyFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing WeeklyFragment")
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
                    Log.d(TAG, "Saldo loaded: weekly=${state.data.weekly}")
                    binding.textWalletWeekly.text = viewModel.formatRupiah(state.data.weekly)
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Error loading saldo: ${state.error}")
                    Toast.makeText(requireContext(), "Gagal memuat saldo: ${state.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}