package com.example.driverangkot.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.usecase.user.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _loginState = MutableLiveData<ResultState<LoginSuccessResponse>>()
    val loginState: LiveData<ResultState<LoginSuccessResponse>> get() = _loginState

    fun login(email: String, password: String) {
        _loginState.value = ResultState.Loading
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _loginState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal login")
            }
        }
    }
}