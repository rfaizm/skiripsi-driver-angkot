package com.example.driverangkot.data.datasource

import android.util.Log
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.data.api.dto.ListPassengerResponse
import com.example.driverangkot.data.preference.UserPreference

class ListPassengersDataSourceImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : ListPassengersDataSource {

    private val TAG = "ListPassengersDataSource"

    override suspend fun getListPassengers(): ListPassengerResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Fetching list passengers with token: Bearer $token")
            return apiService.getListPassengers("Bearer $token")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching list passengers: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getPlaceName(latitude: Double, longitude: Double): GetPlaceNameResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Fetching place name with lat=$latitude, long=$longitude")
            return apiService.getPlaceName("Bearer $token", latitude, longitude)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching place name: ${e.message}", e)
            throw e
        }
    }
}