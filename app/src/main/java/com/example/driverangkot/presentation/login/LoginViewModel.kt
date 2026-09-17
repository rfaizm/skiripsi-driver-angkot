package com.example.driverangkot.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.repository.UserRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.toResultState
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginState = MutableLiveData<ResultState<LoginSuccessResponse>>()
    val loginState: LiveData<ResultState<LoginSuccessResponse>> get() = _loginState

    fun login(email: String, password: String) {
        _loginState.value = ResultState.Loading
        viewModelScope.launch {
            _loginState.value = runCatching { userRepository.login(email, password) }.toResultState("Gagal login")
        }
    }
}
