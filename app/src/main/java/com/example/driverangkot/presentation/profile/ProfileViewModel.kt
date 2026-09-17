package com.example.driverangkot.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.HistoryResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.data.repository.UserRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.toResultState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _logoutState = MutableLiveData<ResultState<LogoutResponse>>()
    val logoutState: LiveData<ResultState<LogoutResponse>> get() = _logoutState

    private val _historyState = MutableLiveData<ResultState<HistoryResponse>>()
    val historyState: LiveData<ResultState<HistoryResponse>> get() = _historyState

    fun logout() {
        _logoutState.value = ResultState.Loading
        viewModelScope.launch {
            _logoutState.value = runCatching { userRepository.logout() }.toResultState("Gagal logout")
        }
    }

    fun getHistory() {
        _historyState.value = ResultState.Loading
        viewModelScope.launch {
            _historyState.value = runCatching { userRepository.getHistory() }.toResultState("Gagal mengambil history")
        }
    }
}
