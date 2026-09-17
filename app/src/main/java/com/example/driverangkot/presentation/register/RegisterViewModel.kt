package com.example.driverangkot.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.data.repository.UserRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.toResultState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<ResultState<RegisterSuccessResponse>>()
    val registerState: LiveData<ResultState<RegisterSuccessResponse>> get() = _registerState

    fun register(
        fullName: String,
        noHp: String,
        noHpEmergency: String,
        email: String,
        trayekId: Int,
        noPlat: String,
        password: String,
        selfPhoto: MultipartBody.Part,
        ktp: MultipartBody.Part,
        sim: MultipartBody.Part,
        stnk: MultipartBody.Part
    ) {
        _registerState.value = ResultState.Loading
        viewModelScope.launch {
            _registerState.value = runCatching {
                userRepository.register(fullName, noHp, noHpEmergency, email, trayekId, noPlat, password, selfPhoto, ktp, sim, stnk)
            }.toResultState("Gagal registrasi")
        }
    }
}
