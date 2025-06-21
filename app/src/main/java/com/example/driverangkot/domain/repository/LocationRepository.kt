package com.example.driverangkot.domain.repository

import com.example.driverangkot.domain.entity.LatLng

interface LocationRepository {
    suspend fun getLastLocation(): LatLng?
}