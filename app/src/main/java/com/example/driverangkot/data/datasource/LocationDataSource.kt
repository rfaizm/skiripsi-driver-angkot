package com.example.driverangkot.data.datasource

import com.example.driverangkot.domain.entity.LatLng

interface LocationDataSource {
    suspend fun getLastLocation(): LatLng?
}