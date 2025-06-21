package com.example.driverangkot.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.usecase.user.LogoutUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _logoutState = MutableLiveData<ResultState<LogoutResponse>>()
    val logoutState: LiveData<ResultState<LogoutResponse>> get() = _logoutState

    fun logout() {
        _logoutState.value = ResultState.Loading
        viewModelScope.launch {
            val result = logoutUseCase()
            _logoutState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal logout")
            }
        }
    }
}