package com.example.driverangkot.data.repository

import android.util.Log
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.data.api.dto.ListPassengerResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.repository.ListPassengersRepository
import retrofit2.HttpException

class ListPassengersRepositoryImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : ListPassengersRepository {

    private val TAG = "ListPassengersRepository"

    override suspend fun getListPassengers(): ListPassengerResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Mengambil daftar penumpang dengan token: Bearer $token")
            val response = apiService.getListPassengers("Bearer $token")
            Log.d(TAG, "Respons getListPassengers: $response")
            return response
        } catch (e: HttpException) {
            Log.d(TAG, "HTTP Error getListPassengers: ${e.code()} - ${e.message()}")
            throw Exception("Gagal mendapatkan daftar penumpang: ${e.message()}")
        } catch (e: Exception) {
            Log.d(TAG, "Error getListPassengers: ${e.message}")
            throw e
        }
    }

    override suspend fun getPlaceName(latitude: Double, longitude: Double): GetPlaceNameResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Mengambil nama tempat: lat=$latitude, long=$longitude")
            val response = apiService.getPlaceName("Bearer $token", latitude, longitude)
            Log.d(TAG, "Respons getPlaceName: $response")
            return response
        } catch (e: HttpException) {
            Log.d(TAG, "HTTP Error getPlaceName: ${e.code()} - ${e.message()}")
            throw Exception("Gagal mendapatkan nama tempat: ${e.message()}")
        } catch (e: Exception) {
            Log.d(TAG, "Error getPlaceName: ${e.message}")
            throw e
        }
    }
}