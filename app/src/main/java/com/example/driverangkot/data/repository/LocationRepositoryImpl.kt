package com.example.driverangkot.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.driverangkot.domain.entity.LatLng
import com.example.driverangkot.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationRepositoryImpl(
    private val context: Context
) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): LatLng? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                LatLng(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }



}