package com.example.driverangkot.data.datasource

import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.data.api.dto.ListPassengerResponse

interface ListPassengersDataSource {
    suspend fun getListPassengers(): ListPassengerResponse
    suspend fun getPlaceName(latitude: Double, longitude: Double): GetPlaceNameResponse
}