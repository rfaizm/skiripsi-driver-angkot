package com.example.driverangkot.presentation.income

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.SaldoData
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.usecase.user.GetDriverSaldoUseCase
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class IncomeViewModel(
    private val getDriverSaldoUseCase: GetDriverSaldoUseCase
) : ViewModel() {

    private val TAG = "IncomeViewModel"

    private val _saldoState = MutableLiveData<ResultState<SaldoData>>()
    val saldoState: LiveData<ResultState<SaldoData>> get() = _saldoState

    init {
        fetchSaldo()
    }

    fun fetchSaldo() {
        _saldoState.value = ResultState.Loading
        viewModelScope.launch {
            when (val result = getDriverSaldoUseCase()) {
                is ResultState.Success -> {
                    val saldoData = result.data.data
                    if (saldoData != null) {
                        _saldoState.value = ResultState.Success(saldoData)
                        Log.d(TAG, "Saldo loaded: daily=${saldoData.daily}, weekly=${saldoData.weekly}, total=${saldoData.total}")
                    } else {
                        _saldoState.value = ResultState.Error("Data saldo tidak tersedia")
                        Log.e(TAG, "Saldo data is null")
                    }
                }
                is ResultState.Error -> {
                    _saldoState.value = ResultState.Error(result.error)
                    Log.e(TAG, "Error fetching saldo: ${result.error}")
                }
                is ResultState.Loading -> {}
            }
        }
    }

    // [Baru] Format saldo ke format Rupiah
    fun formatRupiah(amount: Int?): String {
        return if (amount != null) {
            val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
            "Rp ${formatter.format(amount)}"
        } else {
            "Rp 0"
        }
    }
}