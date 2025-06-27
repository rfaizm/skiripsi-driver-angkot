package com.example.driverangkot.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.driverangkot.data.datasource.LocationDataSource
import com.example.driverangkot.domain.entity.LatLng
import com.example.driverangkot.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): LatLng? {
        try {
            return locationDataSource.getLastLocation()
        } catch (e: Exception) {
            return null
        }
    }
}