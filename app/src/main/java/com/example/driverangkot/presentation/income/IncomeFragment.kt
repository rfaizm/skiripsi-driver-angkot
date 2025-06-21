package com.example.driverangkot.presentation.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.driverangkot.databinding.FragmentIncomeBinding
import com.example.driverangkot.presentation.adapter.IncomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}