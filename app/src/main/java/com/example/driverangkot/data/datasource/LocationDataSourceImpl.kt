package com.example.driverangkot.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import com.example.driverangkot.domain.entity.LatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationDataSourceImpl(
    private val context: Context
) : LocationDataSource {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): LatLng? {
        try {
            val location = fusedLocationClient.lastLocation.await()
            return if (location != null) {
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }
}