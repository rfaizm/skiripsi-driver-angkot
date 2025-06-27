package com.example.driverangkot.presentation.income

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.driverangkot.databinding.FragmentIncomeBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.adapter.IncomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<IncomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val TAG = "IncomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing IncomeFragment")

        setupViewPager()
        observeSaldo()
    }

    private fun setupViewPager() {
        val adapter = IncomePagerAdapter(activity as AppCompatActivity)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Harian"
                1 -> "Mingguan"
                else -> ""
            }
        }.attach()
    }

    private fun observeSaldo() {
        viewModel.saldoState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    showLoading(true)
                    Log.d(TAG, "Loading saldo data")
                    // Tampilkan loading jika diperlukan
                }
                is ResultState.Success -> {
                    showLoading(false)
                    Log.d(TAG, "Saldo loaded: total=${state.data.total}")
                    binding.incomeCard.setSaldo(viewModel.formatRupiah(state.data.total))
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e(TAG, "Error loading saldo: ${state.error}")
                    Toast.makeText(requireContext(), "Gagal memuat saldo: ${state.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}