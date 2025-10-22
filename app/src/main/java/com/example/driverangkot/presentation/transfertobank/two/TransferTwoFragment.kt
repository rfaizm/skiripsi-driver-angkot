package com.example.driverangkot.presentation.transfertobank.two

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.driverangkot.R
import com.example.driverangkot.databinding.FragmentTransferTwoBinding
import com.example.driverangkot.presentation.transfertobank.one.TransferOneFragment


class TransferTwoFragment : Fragment() {

    private var _binding : FragmentTransferTwoBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance() = TransferTwoFragment()
    }
}