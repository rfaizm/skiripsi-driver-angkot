package com.example.driverangkot.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.driverangkot.databinding.FragmentProfileBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLogoutState()
        binding.logoutButton.setOnClickListener {
            profileViewModel.logout()
        }
    }

    private fun observeLogoutState() {
        profileViewModel.logoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.logoutButton.isEnabled = false
                    showLoading(true)
                }
                is ResultState.Success -> {
                    binding.logoutButton.isEnabled = true
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Berhasil!")
                        .setMessage("Anda telah logout.")
                        .setPositiveButton("OK") { _, _ ->
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        .show()
                    showLoading(false)
                }
                is ResultState.Error -> {
                    binding.logoutButton.isEnabled = true
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                    showLoading(false)
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