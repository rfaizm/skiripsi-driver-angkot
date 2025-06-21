package com.example.driverangkot.domain.usecase.angkot

import android.util.Log
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.domain.repository.AngkotRepository

class ToOfflineUseCase(private val angkotRepository: AngkotRepository) {
    suspend operator fun invoke() : Result<ToOfflineResponse>  {
        return try {
            Log.d("ToOfflineUseCase", "Menjalankan toOfflineUseCase")
            val response = angkotRepository.toOffline()
            Log.d("ToOfflineUseCase", "Response: $response")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}