package com.example.driverangkot.domain.usecase.angkot

import android.util.Log
import com.example.driverangkot.data.api.dto.UpdateLocationResponse
import com.example.driverangkot.domain.repository.AngkotRepository

class UpdateLocationUseCase(private val angkotRepository: AngkotRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<UpdateLocationResponse> {
        return try {
            Log.d("UpdateLocationUseCase", "Menjalankan updateLocation: lat=$latitude, long=$longitude")
            val response = angkotRepository.updateLocation(latitude, longitude)
            Log.d("UpdateLocationUseCase", "Response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Log.e("UpdateLocationUseCase", "Error: ${e.message}")
            Result.failure(e)
        }
    }
}