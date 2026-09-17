package com.example.driverangkot.presentation.income

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.SaldoData
import com.example.driverangkot.data.repository.UserRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.toResultState
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class IncomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _saldoState = MutableLiveData<ResultState<SaldoData>>()
    val saldoState: LiveData<ResultState<SaldoData>> get() = _saldoState

    init {
        fetchSaldo()
    }

    fun fetchSaldo() {
        _saldoState.value = ResultState.Loading
        viewModelScope.launch {
            _saldoState.value = runCatching {
                userRepository.getSaldo().data ?: throw Exception("Data saldo tidak tersedia")
            }.toResultState("Gagal mendapatkan saldo driver")
        }
    }

    fun formatRupiah(amount: Int?): String {
        return if (amount != null) {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            "Rp ${formatter.format(amount)}"
        } else {
            "Rp 0"
        }
    }
}
