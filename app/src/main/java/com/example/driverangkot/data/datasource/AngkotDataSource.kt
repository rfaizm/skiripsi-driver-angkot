package com.example.driverangkot.data.datasource

import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.api.dto.UpdateLocationResponse

interface AngkotDataSource {
    suspend fun toOnline(latitude: Double, longitude: Double): ToOnlineResponse
    suspend fun toOffline(): ToOfflineResponse
    suspend fun updateLocation(latitude: Double, longitude: Double): UpdateLocationResponse
}