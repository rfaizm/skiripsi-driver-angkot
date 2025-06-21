package com.example.driverangkot.presentation.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.usecase.user.RegisterUseCase
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
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
            val result = registerUseCase(
                fullName, noHp, noHpEmergency, email, trayekId, noPlat, password,
                selfPhoto, ktp, sim, stnk
            )
            _registerState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal registrasi")
            }
        }
    }
}