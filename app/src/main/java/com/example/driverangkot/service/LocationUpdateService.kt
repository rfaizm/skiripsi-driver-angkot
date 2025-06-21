package com.example.driverangkot.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.di.Injection
import com.example.driverangkot.domain.usecase.angkot.UpdateLocationUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import android.app.NotificationChannel
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class LocationUpdateService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var userPreference: UserPreference
    private lateinit var updateLocationUseCase: UpdateLocationUseCase
    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null
    private val distanceThreshold = 10.0 // Jarak minimum untuk pembaruan (meter)
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isServiceStarted = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userPreference = UserPreference.getInstance(dataStore)
        updateLocationUseCase = Injection.provideUpdateLocationUseCase(this)
        setupLocationCallback()
        Log.d("LocationUpdateService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationUpdateService", "onStartCommand called, isOnline=${userPreference.getStatusOnline()}")
        if (userPreference.getStatusOnline() != true) {
            Log.d("LocationUpdateService", "User offline, stopping service")
            stopSelf()
            isServiceStarted = false
            return START_NOT_STICKY
        }

        if (!isServiceStarted) {
            startForegroundService()
            startLocationUpdates()
            isServiceStarted = true
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        try {
            val channelId = "LocationUpdateChannel"
            val channelName = "Location Updates"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
                )
                val manager = getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
                Log.d("LocationUpdateService", "Notification channel created")
            }

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Pembaruan Lokasi Angkot")
                .setContentText("Mengirim pembaruan lokasi ke server")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build()

            startForeground(1, notification)
            Log.d("LocationUpdateService", "Foreground service started")
        } catch (e: Exception) {
            Log.e("LocationUpdateService", "Failed to start foreground service: ${e.message}")
            stopSelf()
            isServiceStarted = false
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.lastOrNull()?.let { location ->
                    val isOnline = userPreference.getStatusOnline() ?: false
                    if (!isOnline) {
                        Log.d("LocationUpdateService", "User offline, stopping service")
                        stopLocationUpdates()
                        stopSelf()
                        isServiceStarted = false
                        return
                    }

                    val currentLat = location.latitude
                    val currentLon = location.longitude

                    if (shouldUpdateLocation(currentLat, currentLon)) {
                        coroutineScope.launch {
                            try {
                                val result = updateLocationUseCase(currentLat, currentLon)
                                if (result.isSuccess) {
                                    Log.d("LocationUpdateService", "Location updated: lat=$currentLat, long=$currentLon")
                                    lastLatitude = currentLat
                                    lastLongitude = currentLon
                                } else {
                                    Log.e("LocationUpdateService", "Failed to update location: ${result.exceptionOrNull()?.message}")
                                }
                            } catch (e: Exception) {
                                Log.e("LocationUpdateService", "Error updating location: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun shouldUpdateLocation(currentLat: Double, currentLon: Double): Boolean {
        if (lastLatitude == null || lastLongitude == null) {
            return true // Update pertama selalu dikirim
        }

        val distance = calculateDistance(lastLatitude!!, lastLongitude!!, currentLat, currentLon)
        Log.d("LocationUpdateService", "Distance moved: $distance meters")
        return distance >= distanceThreshold
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3 // Radius bumi dalam meter
        val phi1 = lat1 * PI / 180
        val phi2 = lat2 * PI / 180
        val deltaPhi = (lat2 - lat1) * PI / 180
        val deltaLambda = (lon2 - lon1) * PI / 180

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) * sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c // Jarak dalam meter
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationUpdateService", "Location permission not granted, stopping service")
            stopSelf()
            isServiceStarted = false
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(5)
        )
            .setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(2))
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d("LocationUpdateService", "Location updates started")
        } catch (e: SecurityException) {
            Log.e("LocationUpdateService", "Failed to start location updates: ${e.message}")
            stopSelf()
            isServiceStarted = false
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationUpdateService", "Location updates stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        coroutineScope.cancel()
        isServiceStarted = false
        Log.d("LocationUpdateService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}