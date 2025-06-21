package com.example.driverangkot.presentation.map

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.driverangkot.R
import com.example.driverangkot.utils.LocationPermissionListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private var permissionListener: LocationPermissionListener? = null
    private var isMapReady = false
    private val markers = mutableMapOf<String, Marker>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                if (isAdded) {
                    getMyLocation()
                    permissionListener?.onLocationPermissionGranted()
                }
            }
        }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        isMapReady = true

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isIndoorLevelPickerEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        if (isAdded) {
            getMyLocation()
        }
        Log.d("MapsFragment", "Map is ready")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        if (parentFragment is LocationPermissionListener) {
            permissionListener = parentFragment as LocationPermissionListener
        }
    }

    private fun getMyLocation() {
        if (!isAdded || context == null) {
            Log.d("MapsFragment", "getMyLocation skipped: Fragment not attached")
            return
        }
        if (!isMapReady) {
            Log.d("MapsFragment", "getMyLocation skipped: Map not ready")
            return
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            permissionListener?.onLocationPermissionGranted()
            Log.d("MapsFragment", "Location enabled")
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            Log.d("MapsFragment", "Requesting location permission")
        }
    }

    fun animateCameraToLocation(lat: Double, lng: Double) {
        if (!isAdded || !isMapReady) {
            Log.d("MapsFragment", "animateCameraToLocation skipped: Fragment not attached or map not ready")
            return
        }
        val targetLocation = LatLng(lat, lng)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(targetLocation, 15f),
            1000,
            null
        )
        Log.d("MapsFragment", "Camera animated to Lat=$lat, Lng=$lng")
    }

    fun animateCameraToBounds(locations: List<LatLng>) {
        if (!isAdded || !isMapReady) {
            Log.d("MapsFragment", "animateCameraToBounds skipped: Fragment not attached or map not ready")
            return
        }
        if (locations.isEmpty()) {
            Log.d("MapsFragment", "No locations provided for animateCameraToBounds")
            return
        }
        if (locations.size == 1) {
            animateCameraToLocation(locations[0].latitude, locations[0].longitude)
            return
        }

        val builder = LatLngBounds.Builder()
        locations.forEach { builder.include(it) }
        val bounds = builder.build()
        val padding = 100 // Padding dalam piksel
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)

        mMap.animateCamera(
            cameraUpdate,
            1000,
            null
        )
        Log.d("MapsFragment", "Camera animated to bounds with ${locations.size} locations")
    }

    // [Berubah] Modifikasi updateMarker untuk mendukung ikon kustom
    fun updateMarker(id: String, lat: Double, lng: Double, title: String? = null, isStartPoint: Boolean = false) {
        if (!isAdded || !isMapReady) {
            Log.d("MapsFragment", "updateMarker skipped: Fragment not attached or map not ready")
            return
        }
        val position = LatLng(lat, lng)
        markers[id]?.remove()
        val markerOptions = MarkerOptions()
            .position(position)
            .title(title)
        // [Baru] Gunakan ikon kustom berdasarkan isStartPoint
        markerOptions.icon(
            vectorToBitmap(
                if (isStartPoint) R.drawable.ic_location_black else R.drawable.ic_location,
                Color.parseColor(if (isStartPoint) "#000000" else "#f71d05"),
                // Tambahkan parameter ukuran di sini
                100, // Lebar ikon dalam piksel
                100  // Tinggi ikon dalam piksel
            )

        )
        val marker = mMap.addMarker(markerOptions)
        if (marker != null) {
            markers[id] = marker
            Log.d("MapsFragment", "Marker updated for ID $id at Lat=$lat, Lng=$lng, isStartPoint=$isStartPoint")
        } else {
            Log.e("MapsFragment", "Failed to add marker for ID $id")
        }
    }

    // [Baru] Fungsi untuk menghapus marker spesifik berdasarkan orderId
    fun removeMarkersForOrder(orderId: Int) {
        if (!isAdded || !isMapReady) {
            Log.d("MapsFragment", "removeMarkersForOrder skipped: Fragment not attached or map not ready")
            return
        }
        val startId = "starting_point_$orderId"
        val destId = "destination_point_$orderId"
        markers[startId]?.remove()
        markers[destId]?.remove()
        markers.remove(startId)
        markers.remove(destId)
        Log.d("MapsFragment", "Markers removed for orderId=$orderId")
    }

    fun clearMarkers() {
        if (!isAdded || !isMapReady) {
            Log.d("MapsFragment", "clearMarkers skipped: Fragment not attached or map not ready")
            return
        }
        markers.values.forEach { it.remove() }
        markers.clear()
        Log.d("MapsFragment", "All markers cleared")
    }

    // Modifikasi fungsi vectorToBitmap untuk menerima parameter ukuran
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int, width: Int, height: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        // Gunakan ukuran yang diberikan atau ukuran intrinsik jika tidak valid
        val bitmapWidth = if (width > 0) width else vectorDrawable.intrinsicWidth
        val bitmapHeight = if (height > 0) height else vectorDrawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(
            bitmapWidth,
            bitmapHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, bitmapWidth, bitmapHeight)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        permissionListener = null
        markers.values.forEach { it.remove() }
        markers.clear()
        Log.d("MapsFragment", "onDestroyView called")
    }
}