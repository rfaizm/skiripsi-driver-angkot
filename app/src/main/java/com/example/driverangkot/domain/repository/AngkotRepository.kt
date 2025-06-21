package com.example.driverangkot.domain.repository

import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.api.dto.UpdateLocationResponse

interface AngkotRepository {
    suspend fun toOnline(latitude: Double, longitude: Double): ToOnlineResponse
    suspend fun toOffline() : ToOfflineResponse
    suspend fun updateLocation(latitude: Double, longitude: Double): UpdateLocationResponse
}