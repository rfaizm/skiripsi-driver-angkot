package com.example.driverangkot.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.databinding.FragmentProfileBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.adapter.HistoryAdapter
import com.example.driverangkot.presentation.login.LoginActivity
import com.example.driverangkot.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val userPreference by lazy {
        UserPreference.getInstance(requireContext().dataStore)
    }

    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Initializing ProfileFragment")
        setupUserProfile()
        setupRecyclerView()
        observeLogoutState()
        observeHistoryState()
        binding.logoutButton.setOnClickListener {
            profileViewModel.logout()
        }
        // [Baru] Panggil getHistory saat fragment dimuat
        profileViewModel.getHistory()
    }

    private fun setupUserProfile() {
        val name = userPreference.getName()
        val email = userPreference.getEmail()
        val trayekId = userPreference.getTrayekId()

        Log.d(TAG, "User profile: name=$name, email=$email, trayekId=$trayekId")

        // Bind nama ke TextView
        if (name != null) {
            binding.fullname.text = name
        } else {
            Log.e(TAG, "Name not found in UserPreference")
            binding.fullname.text = "Nama Tidak Ditemukan"
        }

        // Bind email ke TextView
        if (email != null) {
            binding.gmailText.text = email
        } else {
            Log.e(TAG, "Email not found in UserPreference")
            binding.gmailText.text = "Email Tidak Ditemukan"
        }

        // Bind nama trayek ke TextView
        val trayekName = Utils.getTrayekName(requireContext(), trayekId.toString())
        binding.trayekName.text = trayekName
        if (trayekName == "Trayek Tidak Ditemukan") {
            Log.e(TAG, "Trayek ID not found or invalid in UserPreference: $trayekId")
        }
    }

    // [Baru] Setup RecyclerView
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = historyAdapter
    }

    private fun observeLogoutState() {
        profileViewModel.logoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    Log.d(TAG, "Logging out, showing loading")
                    binding.logoutButton.isEnabled = false
                    showLoading(true)
                }
                is ResultState.Success -> {
                    Log.d(TAG, "Logout successful")
                    binding.logoutButton.isEnabled = true
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                    showLoading(false)
                }
                is ResultState.Error -> {
                    Log.e(TAG, "Error logging out: ${state.error}")
                    binding.logoutButton.isEnabled = true
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    showLoading(false)
                }
            }
        }
    }

    // [Baru] Observe history state
    private fun observeHistoryState() {
        profileViewModel.historyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    Log.d(TAG, "Loading history")
                    showLoading(true)
                }

                is ResultState.Success -> {
                    Log.d(TAG, "History fetched: ${state.data.data}")
                    showLoading(false)

                    val historyList = state.data.data?.filterNotNull() ?: emptyList()

                    if (historyList.isEmpty()) {
                        binding.historyEmpty.visibility = View.VISIBLE
                        binding.textHistoryEmpty.visibility = View.VISIBLE
                        binding.rvHistory.visibility = View.GONE
                    } else {
                        binding.historyEmpty.visibility = View.GONE
                        binding.textHistoryEmpty.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE

                        historyAdapter.submitList(historyList)
                    }
                }

                is ResultState.Error -> {
                    Log.e(TAG, "Error fetching history: ${state.error}")
                    showLoading(false)

                    binding.historyEmpty.visibility = View.VISIBLE
                    binding.textHistoryEmpty.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Gagal mengambil history: ${state.error}",
                        Toast.LENGTH_SHORT
                    ).show()

                    historyAdapter.submitList(emptyList())
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

    companion object {
        private const val TAG = "ProfileFragment"
    }
}