package com.example.driverangkot.domain.usecase.user

import android.util.Log
import com.example.driverangkot.data.api.dto.SaldoDriverResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.repository.UserRepository

class GetDriverSaldoUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): ResultState<SaldoDriverResponse> {
        return try {
            Log.d("GetDriverSaldoUseCase", "Menjalankan getDriverSaldoUseCase")
            val response = userRepository.getSaldo()
            Log.d("GetDriverSaldoUseCase", "Response: $response")
            ResultState.Success(response)
        } catch (e: Exception) {
            Log.e("GetDriverSaldoUseCase", "Error: ${e.message}", e)
            ResultState.Error(e.message ?: "Gagal mendapatkan saldo driver")
        }
    }
}