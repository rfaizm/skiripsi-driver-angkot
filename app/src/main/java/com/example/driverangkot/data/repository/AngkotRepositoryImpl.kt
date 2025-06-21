package com.example.driverangkot.data.repository

import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.api.dto.UpdateLocationResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.repository.AngkotRepository
import retrofit2.HttpException

class AngkotRepositoryImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : AngkotRepository {
    override suspend fun toOnline(latitude: Double, longitude: Double): ToOnlineResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            val response = apiService.toOnline("Bearer $token", latitude, longitude)
            return response
        } catch (e: HttpException) {
            throw Exception("Gagal online: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun toOffline(): ToOfflineResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw  Exception("Token tidak ditemukan")
            val response = apiService.toOffline("Bearer $token")
            return response
        } catch (e: HttpException) {
            throw Exception("Gagal offline: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateLocation(latitude: Double, longitude: Double): UpdateLocationResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            val response = apiService.updateLocation("Bearer $token", latitude, longitude)
            return response
        } catch (e: HttpException) {
            throw Exception("Gagal memperbarui lokasi: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

}