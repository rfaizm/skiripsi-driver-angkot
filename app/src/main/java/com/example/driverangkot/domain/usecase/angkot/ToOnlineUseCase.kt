package com.example.driverangkot.domain.usecase.angkot

import android.util.Log
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.domain.repository.AngkotRepository

class ToOnlineUseCase(private val angkotRepository: AngkotRepository) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<ToOnlineResponse> {
        return try {
            Log.d("ToOnlineUseCase", "Latitude: $latitude, Longitude: $longitude")
            val response = angkotRepository.toOnline(latitude, longitude)
            Log.d("ToOnlineUseCase", "Response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}