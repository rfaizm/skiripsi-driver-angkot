package com.example.driverangkot.domain.repository

import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.data.api.dto.ListPassengerResponse

interface ListPassengersRepository {
    suspend fun getListPassengers(): ListPassengerResponse
    suspend fun getPlaceName(latitude: Double, longitude: Double): GetPlaceNameResponse
}