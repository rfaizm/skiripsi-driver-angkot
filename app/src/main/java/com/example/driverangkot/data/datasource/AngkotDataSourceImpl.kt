package com.example.driverangkot.data.datasource

import android.util.Log
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.api.dto.UpdateLocationResponse
import com.example.driverangkot.data.preference.UserPreference

class AngkotDataSourceImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : AngkotDataSource {

    private val TAG = "AngkotDataSourceImpl"

    override suspend fun toOnline(latitude: Double, longitude: Double): ToOnlineResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Calling toOnline with lat=$latitude, long=$longitude")
            return apiService.toOnline("Bearer $token", latitude, longitude)
        } catch (e: Exception) {
            Log.e(TAG, "Error in toOnline: ${e.message}", e)
            throw e
        }
    }

    override suspend fun toOffline(): ToOfflineResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Calling toOffline")
            return apiService.toOffline("Bearer $token")
        } catch (e: Exception) {
            Log.e(TAG, "Error in toOffline: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateLocation(latitude: Double, longitude: Double): UpdateLocationResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Calling updateLocation with lat=$latitude, long=$longitude")
            return apiService.updateLocation("Bearer $token", latitude, longitude)
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateLocation: ${e.message}", e)
            throw e
        }
    }
}