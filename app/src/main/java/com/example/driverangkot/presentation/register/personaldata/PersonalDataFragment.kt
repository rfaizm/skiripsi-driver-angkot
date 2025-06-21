package com.example.driverangkot.presentation.register.personaldata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.driverangkot.R
import com.example.driverangkot.databinding.FragmentPersonalDataBinding
import com.example.driverangkot.domain.entity.Trayek
import com.example.driverangkot.presentation.adapter.RouteArrayAdapter
import com.example.driverangkot.presentation.register.document.DocumentFragment
import com.google.android.material.snackbar.Snackbar


class PersonalDataFragment : Fragment() {

    private var _binding: FragmentPersonalDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var trayekList: List<Trayek>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTrayekList()
        setupRouteDropdown()
        setupButton()
    }

    private fun setupTrayekList() {
        val trayekArray = resources.getStringArray(R.array.trayeks)
        trayekList = trayekArray.map { trayek ->
            val (id, name) = trayek.split(":", limit = 2)
            Trayek(id.trim(), name.trim())
        }
    }

    private fun setupRouteDropdown() {
        val adapter = RouteArrayAdapter(requireContext(), R.layout.dropdown_item, trayekList)
        binding.inputRouteAngkot.setAdapter(adapter)
        binding.inputRouteAngkot.threshold = 1
        // Set teks dropdown ke nama trayek saat item dipilih
        binding.inputRouteAngkot.setOnItemClickListener { _, _, position, _ ->
            val selectedTrayek = adapter.getItem(position)
            binding.inputRouteAngkot.setText(selectedTrayek?.name, false)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.inputFullname.text.toString().trim().isEmpty()) {
            binding.layoutFullname.error = "Nama lengkap harus diisi"
            isValid = false
        } else {
            binding.layoutFullname.error = null
        }

        if (binding.inputNoHpActive.text.toString().trim().isEmpty()) {
            binding.layoutNoHpActive.error = "Nomor HP aktif harus diisi"
            isValid = false
        } else {
            binding.layoutNoHpActive.error = null
        }

        if (binding.inputNoHpEmergency.text.toString().trim().isEmpty()) {
            binding.layoutNoHpEmergency.error = "Nomor HP darurat harus diisi"
            isValid = false
        } else {
            binding.layoutNoHpEmergency.error = null
        }

        if (binding.inputEmail.text.toString().trim().isEmpty()) {
            binding.layoutEmail.error = "Email harus diisi"
            isValid = false
        } else {
            binding.layoutEmail.error = null
        }

        if (binding.inputRouteAngkot.text.toString().trim().isEmpty()) {
            binding.layoutRouteAngkot.error = "Rute angkot harus diisi"
            isValid = false
        } else {
            binding.layoutRouteAngkot.error = null
        }

        if (binding.inputPlatVehicle.text.toString().trim().isEmpty()) {
            binding.layoutPlatVehicle.error = "Plat kendaraan harus diisi"
            isValid = false
        } else {
            binding.layoutPlatVehicle.error = null
        }

        if (binding.inputPassword.text.toString().trim().isEmpty()) {
            binding.layoutPassword.error = "Password harus diisi"
            isValid = false
        } else {
            binding.layoutPassword.error = null
        }

        return isValid
    }

    private fun setupButton() {
        binding.buttonNext.setOnClickListener {
            if (validateInputs()) {
                val selectedTrayek = trayekList.find {
                    it.name == binding.inputRouteAngkot.text.toString().trim()
                }
                val documentFragment = DocumentFragment.newInstance()
                val bundle = Bundle().apply {
                    putString("fullname", binding.inputFullname.text.toString().trim())
                    putString("noHp", binding.inputNoHpActive.text.toString().trim())
                    putString("noHpEmergency", binding.inputNoHpEmergency.text.toString().trim())
                    putString("email", binding.inputEmail.text.toString().trim())
                    putString("password", binding.inputPassword.text.toString().trim())
                    putString("routeAngkot", selectedTrayek?.id ?: "")
                    putString("platVehicle", binding.inputPlatVehicle.text.toString().trim())
                }
                documentFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, documentFragment)
                    .addToBackStack("documentFragment")
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PersonalDataFragment()
    }
}