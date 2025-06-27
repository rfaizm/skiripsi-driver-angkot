package com.example.driverangkot.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.HistoryResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.usecase.user.GetHistoryUseCase
import com.example.driverangkot.domain.usecase.user.LogoutUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getHistoryUseCase: GetHistoryUseCase // [Baru]
) : ViewModel() {

    private val _logoutState = MutableLiveData<ResultState<LogoutResponse>>()
    val logoutState: LiveData<ResultState<LogoutResponse>> get() = _logoutState

    // [Baru] State untuk history
    private val _historyState = MutableLiveData<ResultState<HistoryResponse>>()
    val historyState: LiveData<ResultState<HistoryResponse>> get() = _historyState

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

    // [Berubah] Fungsi untuk mengambil history menggunakan GetHistoryUseCase
    fun getHistory() {
        _historyState.value = ResultState.Loading
        viewModelScope.launch {
            val result = getHistoryUseCase()
            _historyState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal mengambil history")
            }
        }
    }
}