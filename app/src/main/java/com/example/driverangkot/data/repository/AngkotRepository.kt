package com.example.driverangkot.data.repository

import com.example.driverangkot.data.api.ApiService

class AngkotRepository(private val apiService: ApiService) {

    suspend fun toOnline(latitude: Double, longitude: Double) = apiService.toOnline(latitude, longitude)

    suspend fun toOffline() = apiService.toOffline()

    suspend fun updateLocation(latitude: Double, longitude: Double) = apiService.updateLocation(latitude, longitude)
}
