package com.example.driverangkot.domain.usecase.listpassenger

import android.util.Log
import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.repository.ListPassengersRepository

class GetPlaceNameUseCase(
    private val listPassengersRepository: ListPassengersRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): ResultState<GetPlaceNameResponse> {
        return try {
            Log.d("GetPlaceNameUseCase", "Menjalankan getPlaceNameUseCase: lat=$latitude, long=$longitude")
            val response = listPassengersRepository.getPlaceName(latitude, longitude)
            Log.d("GetPlaceNameUseCase", "Response: $response")
            if (response.data?.placeName.isNullOrEmpty()) {
                Log.d("GetPlaceNameUseCase", "placeName is null or empty in response")
                ResultState.Error("Nama tempat tidak tersedia")
            } else {
                ResultState.Success(response)
            }
        } catch (e: Exception) {
            Log.d("GetPlaceNameUseCase", "Error: ${e.message}")
            ResultState.Error(e.message ?: "Gagal mendapatkan nama tempat")
        }
    }
}