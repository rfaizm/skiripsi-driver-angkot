package com.example.driverangkot.presentation.register.document

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.driverangkot.databinding.FragmentDocumentBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.presentation.login.LoginActivity
import com.example.driverangkot.presentation.register.RegisterViewModel
import com.example.driverangkot.utils.Utils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class DocumentFragment : Fragment() {

    private var _binding: FragmentDocumentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var fullname: String? = null
    private var noHp: String? = null
    private var noHpEmergency: String? = null
    private var email: String? = null
    private var trayekId: Int? = null
    private var noPlat: String? = null
    private var password: String? = null

    private var selfPhotoUri: Uri? = null
    private var ktpUri: Uri? = null
    private var simUri: Uri? = null
    private var stnkUri: Uri? = null

    private var currentDocumentType: String? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            when (currentDocumentType) {
                "self_photo" -> {
                    selfPhotoUri = uri
                    binding.uploadSelfPhoto.setImageUri(uri)
                }
                "ktp" -> {
                    ktpUri = uri
                    binding.uploadKtp.setImageUri(uri)
                }
                "sim" -> {
                    simUri = uri
                    binding.uploadSim.setImageUri(uri)
                }
                "stnk" -> {
                    stnkUri = uri
                    binding.uploadStnk.setImageUri(uri)
                }
            }
            Log.d("Photo Picker", "Selected URI: $uri for $currentDocumentType")
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fullname = it.getString("fullname")
            noHp = it.getString("noHp")
            noHpEmergency = it.getString("noHpEmergency")
            email = it.getString("email")
            trayekId = it.getString("routeAngkot")?.toIntOrNull()
            noPlat = it.getString("platVehicle")
            password = it.getString("password")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUploadListeners()
        setupInitialTitles()
        setupDescriptionText()
        setupButton()
        observeRegisterState()
    }

    private fun setupUploadListeners() {
        binding.uploadSelfPhoto.setOnClickListener {
            currentDocumentType = "self_photo"
            startGallery()
        }
        binding.uploadKtp.setOnClickListener {
            currentDocumentType = "ktp"
            startGallery()
        }
        binding.uploadSim.setOnClickListener {
            currentDocumentType = "sim"
            startGallery()
        }
        binding.uploadStnk.setOnClickListener {
            currentDocumentType = "stnk"
            startGallery()
        }
    }

    private fun setupInitialTitles() {
        binding.uploadSelfPhoto.setTextTitle("Foto Diri")
        binding.uploadKtp.setTextTitle("KTP")
        binding.uploadSim.setTextTitle("SIM")
        binding.uploadStnk.setTextTitle("STNK")
    }

    private fun setupDescriptionText() {
        if (fullname != null && noHp != null && email != null) {
            binding.titleName.text = fullname
            binding.titleNoHp.text = noHp
            binding.titleEmail.text = email
        } else {
            Log.d("DocumentFragment", "Data tidak lengkap")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupButton() {
        binding.buttonSend.setOnClickListener {
            if (validateInputs()) {
                val selfPhotoFile = selfPhotoUri?.let { Utils.uriToFile(it, requireContext()) }
                val ktpFile = ktpUri?.let { Utils.uriToFile(it, requireContext()) }
                val simFile = simUri?.let { Utils.uriToFile(it, requireContext()) }
                val stnkFile = stnkUri?.let { Utils.uriToFile(it, requireContext()) }

                if (selfPhotoFile != null && ktpFile != null && simFile != null && stnkFile != null) {
                    val selfPhotoPart = MultipartBody.Part.createFormData(
                        "self_photo", selfPhotoFile.name, selfPhotoFile.asRequestBody("image/jpeg".toMediaType())
                    )
                    val ktpPart = MultipartBody.Part.createFormData(
                        "ktp_photo", ktpFile.name, ktpFile.asRequestBody("image/jpeg".toMediaType())
                    )
                    val simPart = MultipartBody.Part.createFormData(
                        "sim_photo", simFile.name, simFile.asRequestBody("image/jpeg".toMediaType())
                    )
                    val stnkPart = MultipartBody.Part.createFormData(
                        "stnk_photo", stnkFile.name, stnkFile.asRequestBody("image/jpeg".toMediaType())
                    )

                    viewModel.register(
                        fullName = fullname!!,
                        noHp = noHp!!,
                        noHpEmergency = noHpEmergency!!,
                        email = email!!,
                        trayekId = trayekId!!,
                        noPlat = noPlat!!,
                        password = password!!,
                        selfPhoto = selfPhotoPart,
                        ktp = ktpPart,
                        sim = simPart,
                        stnk = stnkPart
                    )
                } else {
                    Toast.makeText(requireContext(), "Semua dokumen harus diunggah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (fullname.isNullOrEmpty()) {
            Log.d("DocumentFragment", "Fullname tidak boleh kosong")
            isValid = false
        }
        if (noHp.isNullOrEmpty()) {
            Log.d("DocumentFragment", "No HP tidak boleh kosong")
            isValid = false
        }
        if (noHpEmergency.isNullOrEmpty()) {
            Log.d("DocumentFragment", "No HP darurat tidak boleh kosong")
            isValid = false
        }
        if (email.isNullOrEmpty()) {
            Log.d("DocumentFragment", "Email tidak boleh kosong")
            isValid = false
        }
        if (trayekId == null) {
            Log.d("DocumentFragment", "Trayek ID tidak boleh kosong atau tidak valid")
            isValid = false
        }
        if (noPlat.isNullOrEmpty()) {
            Log.d("DocumentFragment", "No plat tidak boleh kosong")
            isValid = false
        }
        if (password.isNullOrEmpty()) {
            Log.d("DocumentFragment", "Password tidak boleh kosong")
            isValid = false
        }
        if ((password?.length ?: 0) < 8) {
            Log.d("DocumentFragment", "Password minimal 8 karakter")
            isValid = false
        }
        if (selfPhotoUri == null) {
            Toast.makeText(requireContext(), "Foto diri harus diunggah", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (ktpUri == null) {
            Toast.makeText(requireContext(), "KTP harus diunggah", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (simUri == null) {
            Toast.makeText(requireContext(), "SIM harus diunggah", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (stnkUri == null) {
            Toast.makeText(requireContext(), "STNK harus diunggah", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(requireContext(), "Periksa kembali data yang dimasukkan", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun observeRegisterState() {
        viewModel.registerState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ResultState.Loading -> {
                    showLoading(true)
                    binding.buttonSend.isEnabled = false
                }
                is ResultState.Success -> {
                    showLoading(false)
                    binding.buttonSend.isEnabled = true
                    selfPhotoUri?.let { Utils.uriToFile(it, requireContext()).delete() }
                    ktpUri?.let { Utils.uriToFile(it, requireContext()).delete() }
                    simUri?.let { Utils.uriToFile(it, requireContext()).delete() }
                    stnkUri?.let { Utils.uriToFile(it, requireContext()).delete() }
                    Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    // Intent to LoginActivity
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                is ResultState.Error -> {
                    showLoading(false)
                    binding.buttonSend.isEnabled = true
                    Toast.makeText(requireContext(), "Registrasi gagal: ${state.error}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DocumentFragment()
    }
}