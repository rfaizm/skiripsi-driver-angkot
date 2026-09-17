package com.example.driverangkot.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

class LocationRepository(private val context: Context) {

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): LatLng? = runCatching {
        LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
            ?.let { LatLng(it.latitude, it.longitude) }
    }.getOrNull()
}
