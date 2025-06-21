package com.example.driverangkot.domain.usecase.listpassenger

import android.util.Log
import com.example.driverangkot.data.api.dto.ListPassengerResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.repository.ListPassengersRepository

// [Baru] Use case untuk mendapatkan daftar penumpang
class GetListPassengersUseCase(
    private val listPassengersRepository: ListPassengersRepository
) {
    suspend operator fun invoke(): ResultState<ListPassengerResponse> {
        return try {
            Log.d("GetListPassengersUseCase", "Menjalankan getListPassengersUseCase")
            val response = listPassengersRepository.getListPassengers()
            Log.d("GetListPassengersUseCase", "Response: $response")
            ResultState.Success(response)
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Gagal mendapatkan daftar penumpang")
        }
    }
}