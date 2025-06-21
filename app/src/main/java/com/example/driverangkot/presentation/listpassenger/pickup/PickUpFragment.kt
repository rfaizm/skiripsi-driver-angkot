package com.example.driverangkot.presentation.listpassenger.pickup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driverangkot.databinding.FragmentPickUpBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.adapter.ListPassengerAdapter
import com.example.driverangkot.domain.entity.Passenger
import com.example.driverangkot.presentation.listpassenger.ListPassengerActivity
import com.example.driverangkot.presentation.listpassenger.ListPassengersViewModel
import kotlinx.coroutines.launch


class PickUpFragment : Fragment() {

    private var _binding: FragmentPickUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListPassengersViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: ListPassengerAdapter
    private val TAG = "PickUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing PickUpFragment")
        setupRecyclerView()
        observePassengers()
        observeUpdateStatus()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Fetching passengers for PickUpFragment")
        viewModel.fetchPassengers() // [Baru] Refresh data saat fragment aktif
    }

    private fun setupRecyclerView() {
        adapter = ListPassengerAdapter(emptyList()) { passenger ->
            Log.d(TAG, "Slide completed for passenger: orderId=${passenger.orderId}")
            viewModel.updateOrderStatus(passenger.orderId, "selesai")
        }
        binding.rvListPickupPassenger.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@PickUpFragment.adapter
        }
    }

    private fun observePassengers() {
        viewModel.pickedUpPassengersState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    Log.d(TAG, "Loading picked up passengers")
                    (activity as? ListPassengerActivity)?.showLoading(true)
                    binding.rvListPickupPassenger.visibility = View.GONE
                }
                is ResultState.Success -> {
                    Log.d(TAG, "Picked up passengers loaded: ${state.data.size}")
                    (activity as? ListPassengerActivity)?.showLoading(false)
                    binding.rvListPickupPassenger.visibility = View.VISIBLE
                    adapter.updatePassengers(state.data)
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Error loading picked up passengers: ${state.error}")
                    (activity as? ListPassengerActivity)?.showLoading(false)
                    binding.rvListPickupPassenger.visibility = View.GONE
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeUpdateStatus() {
        viewModel.updateStatusState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    Log.d(TAG, "Updating status, showing loading")
                    (activity as? ListPassengerActivity)?.showLoading(true)
                }
                is ResultState.Success -> {
                    Log.d(TAG, "Status updated successfully")
                    (activity as? ListPassengerActivity)?.showLoading(false)
                    Toast.makeText(requireContext(), "Status pesanan diperbarui", Toast.LENGTH_SHORT).show()
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Error updating status: ${state.error}")
                    (activity as? ListPassengerActivity)?.showLoading(false)
                    Toast.makeText(requireContext(), "Gagal memperbarui status: ${state.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PickUpFragment()
    }
}