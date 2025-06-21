package com.example.driverangkot.presentation.home

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.driverangkot.R
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.databinding.FragmentHomeBinding
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.ViewModelFactory
import com.example.driverangkot.domain.entity.OrderData
import com.example.driverangkot.presentation.listpassenger.ListPassengerActivity
import com.example.driverangkot.presentation.map.MapsFragment
import com.example.driverangkot.service.LocationUpdateService
import com.example.driverangkot.service.PusherService
import com.example.driverangkot.utils.LocationPermissionListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment(), LocationPermissionListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isLocationUpdatesStarted = false
    private lateinit var userPreference: UserPreference

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        //  Mulai PusherService hanya jika driver online
        val isOnline = userPreference.getStatusOnline() ?: false
        if (isOnline) {
            startPusherService()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isOnline = userPreference.getStatusOnline() ?: false
        updateOnlineStatus(isOnline)

        setupFAB()
        loadMaps()
        goToListPassengers()
        observeLocationState()
        observeToOnlineState()
        observeToOfflineState()
        observeOrdersState()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.lastOrNull()?.let { location ->
                    if (location.hasSpeed()) {
                        val speedMs = location.speed
                        val speedKmh = speedMs * 3.6
                        val formattedSpeed = if (speedMs.isNaN() || speedMs < 0) {
                            "0 km/j"
                        } else {
                            "${speedKmh.toInt()} km/j"
                        }
                        requireActivity().runOnUiThread {
                            _binding?.fabSpeed?.text = formattedSpeed
                            Log.d("HomeFragment", "Speed: $formattedSpeed")
                        }
                    } else {
                        Log.d("HomeFragment", "Speed data not available")
                        requireActivity().runOnUiThread {
                            _binding?.fabSpeed?.text = "0 km/j"
                        }
                    }
                } ?: run {
                    Log.d("HomeFragment", "No location available for speed")
                    requireActivity().runOnUiThread {
                        _binding?.fabSpeed?.text = "0 km/j"
                    }
                }
            }
        }

        homeViewModel.getUserLocation()
    }

    private fun startPusherService() {
        if (!isServiceRunning(PusherService::class.java)) {
            val serviceIntent = Intent(requireContext(), PusherService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(serviceIntent)
            } else {
                requireContext().startService(serviceIntent)
            }
            Log.d("HomeFragment", "PusherService started")
        }
    }

    // Hentikan PusherService
    private fun stopPusherService() {
        if (isServiceRunning(PusherService::class.java)) {
            val serviceIntent = Intent(requireContext(), PusherService::class.java)
            requireContext().stopService(serviceIntent)
            Log.d("HomeFragment", "PusherService stopped")
        }
    }

    private fun observeOrdersState() {
        homeViewModel.ordersState.observe(viewLifecycleOwner) { orders ->
            val mapsFragment = childFragmentManager.findFragmentById(R.id.map_driver) as? MapsFragment
            mapsFragment?.clearMarkers()
            val locations = mutableListOf<LatLng>()
            orders.forEach { (orderId, order) ->
                if (order.status != "selesai") {
                    mapsFragment?.updateMarker(
                        id = "starting_point_$orderId",
                        lat = order.startLat,
                        lng = order.startLong,
                        title = "Titik Jemput #${order.orderId}",
                        isStartPoint = true
                    )
                    mapsFragment?.updateMarker(
                        id = "destination_point_$orderId",
                        lat = order.destLat,
                        lng = order.destLong,
                        title = "Tujuan #${order.orderId}",
                        isStartPoint = false
                    )
                    locations.add(LatLng(order.startLat, order.startLong))
                    locations.add(LatLng(order.destLat, order.destLong))
                }
            }
            if (locations.isNotEmpty()) {
                mapsFragment?.animateCameraToBounds(locations)
                Log.d("HomeFragment", "Updated ${locations.size/2} orders' markers")
            } else {
                userLatitude?.let { lat ->
                    userLongitude?.let { lng ->
                        mapsFragment?.animateCameraToLocation(lat, lng)
                        Log.d("HomeFragment", "No active orders, camera set to user location")
                    }
                }
            }
        }
    }

    private fun goToListPassengers() {
        binding.fabListPenumpang.setOnClickListener {
            val intent = Intent(requireContext(), ListPassengerActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
        }
    }

    private fun setupFAB() {
        binding.fabPower.setOnClickListener {
            val isOnline = userPreference.getStatusOnline() ?: false
            if (isOnline) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin ingin offline?")
                    .setPositiveButton("Ya") { _, _ ->
                        homeViewModel.toOffline()
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            } else {
                if (userLatitude != null && userLongitude != null) {
                    homeViewModel.toOnline(userLatitude!!, userLongitude!!)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lokasi belum tersedia. Coba lagi.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateOnlineStatus(isOnline: Boolean) {
        val bgColor = if (isOnline) R.color.greenOnline else R.color.black
        val iconTint = if (isOnline) android.R.color.black else android.R.color.white
        Log.d("HomeFragment", "updateOnlineStatus: isOnline=$isOnline, bgColor=$bgColor, iconTint=$iconTint")

        requireActivity().runOnUiThread {
            _binding?.let { binding ->
                binding.fabPower.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), bgColor)
                )
                binding.fabPower.setImageTintList(
                    ContextCompat.getColorStateList(requireContext(), iconTint)
                )
                binding.layoutOnline.setOnlineStatus(if (isOnline) "ONLINE" else "OFFLINE")
            } ?: Log.e("HomeFragment", "Binding is null in updateOnlineStatus")
        }
    }

    private fun loadMaps() {
        val layoutPositionMaps = binding.root.findViewById<View>(R.id.map_driver)
        if (layoutPositionMaps == null) {
            Log.d("HomeFragment", "FrameLayout with ID map_driver not found in layout")
            return
        }
        val existingFragment = childFragmentManager.findFragmentById(R.id.map_driver)
        if (existingFragment == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.map_driver, MapsFragment())
                .commitAllowingStateLoss()
            Log.d("HomeFragment", "MapsFragment attached")
        } else {
            Log.d("HomeFragment", "MapsFragment already exists")
        }
    }

    private fun observeLocationState() {
        homeViewModel.locationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    val latLng = state.data
                    userLatitude = latLng.latitude
                    userLongitude = latLng.longitude
                    Toast.makeText(
                        requireContext(),
                        "Lokasi: Lat=${latLng.latitude}, Lng=${latLng.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                    val mapsFragment = childFragmentManager.findFragmentById(R.id.map_driver) as? MapsFragment
                    mapsFragment?.animateCameraToLocation(latLng.latitude, latLng.longitude)
                    startLocationUpdates()
                    showLoading(false)
                    Log.d("HomeFragment", "Location updated: Lat=${latLng.latitude}, Lng=${latLng.longitude}")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.d("HomeFragment", "Error mendapatkan lokasi: ${state.error}")
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun observeToOnlineState() {
        homeViewModel.toOnlineState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    runBlocking {
                        userPreference.saveOnlineStatus(true)
                    }
                    updateOnlineStatus(true)
                    if (!isServiceRunning(LocationUpdateService::class.java)) {
                        val serviceIntent = Intent(requireContext(), LocationUpdateService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requireContext().startForegroundService(serviceIntent)
                        } else {
                            requireContext().startService(serviceIntent)
                        }
                        Log.d("HomeFragment", "LocationUpdateService started")
                    }
                    // [Baru] Mulai PusherService saat online
                    startPusherService()
                    Toast.makeText(requireContext(), "Anda sudah online", Toast.LENGTH_SHORT).show()
                    Log.d("HomeFragment", "Online status updated: ${state.data}")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    runBlocking {
                        userPreference.saveOnlineStatus(false)
                    }
                    updateOnlineStatus(false)
                    if (isServiceRunning(LocationUpdateService::class.java)) {
                        val serviceIntent = Intent(requireContext(), LocationUpdateService::class.java)
                        requireContext().stopService(serviceIntent)
                        Log.d("HomeFragment", "LocationUpdateService stopped due to error")
                    }
                    Toast.makeText(requireContext(), "Gagal mengubah status: ${state.error}", Toast.LENGTH_LONG).show()
                    Log.d("HomeFragment", "Error updating online status: ${state.error}")
                }
            }
        }
    }

    private fun observeToOfflineState() {
        homeViewModel.toOfflineState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    runBlocking {
                        userPreference.saveOnlineStatus(false)
                    }
                    updateOnlineStatus(false)
                    if (isServiceRunning(LocationUpdateService::class.java)) {
                        val serviceIntent = Intent(requireContext(), LocationUpdateService::class.java)
                        requireContext().stopService(serviceIntent)
                        Log.d("HomeFragment", "LocationUpdateService stopped")
                    }
                    // Hentikan PusherService saat offline
                    stopPusherService()
                    Toast.makeText(requireContext(), "Anda sudah offline", Toast.LENGTH_SHORT).show()
                    Log.d("HomeFragment", "Offline status updated: ${state.data}")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    updateOnlineStatus(userPreference.getStatusOnline() ?: false)
                    Toast.makeText(requireContext(), "Gagal mengubah status: ${state.error}", Toast.LENGTH_LONG).show()
                    Log.d("HomeFragment", "Error updating offline status: ${state.error}")
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (!isAdded || getContext() == null) {
            Log.d("HomeFragment", "startLocationUpdates skipped: Fragment not attached")
            return
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("HomeFragment", "startLocationUpdates skipped: Location permission not granted")
            return
        }
        if (isLocationUpdatesStarted) {
            Log.d("HomeFragment", "Location updates already started")
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
            isLocationUpdatesStarted = true
            Log.d("HomeFragment", "Location updates started")
        } catch (e: SecurityException) {
            Log.d("HomeFragment", "Failed to start location updates: ${e.message}")
            Toast.makeText(requireContext(), "Gagal memulai pembaruan lokasi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        if (isLocationUpdatesStarted && ::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isLocationUpdatesStarted = false
            Log.d("HomeFragment", "Location updates stopped")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onLocationPermissionGranted() {
        homeViewModel.getUserLocation()
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        val isOnline = userPreference.getStatusOnline() ?: false
        updateOnlineStatus(isOnline)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationUpdates()
        _binding = null
    }
}