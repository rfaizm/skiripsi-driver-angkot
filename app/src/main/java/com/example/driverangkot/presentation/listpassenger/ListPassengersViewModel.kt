package com.example.driverangkot.presentation.listpassenger

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.PickedUpItemJSON
import com.example.driverangkot.data.api.dto.WaitingItemJSON
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.entity.Passenger
import com.example.driverangkot.domain.usecase.listpassenger.GetListPassengersUseCase
import com.example.driverangkot.domain.usecase.listpassenger.GetPlaceNameUseCase
import com.example.driverangkot.domain.usecase.location.GetUserLocationUseCase
import com.example.driverangkot.domain.usecase.order.UpdateOrderStatusUseCase
import com.example.driverangkot.utils.Utils
import kotlinx.coroutines.launch

class ListPassengersViewModel(
    private val getListPassengersUseCase: GetListPassengersUseCase,
    private val getPlaceNameUseCase: GetPlaceNameUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase
) : ViewModel() {

    private val TAG = "ListPassengersViewModel"

    private val _waitingPassengersState = MutableLiveData<ResultState<List<Passenger>>>()
    val waitingPassengersState: LiveData<ResultState<List<Passenger>>> get() = _waitingPassengersState

    private val _pickedUpPassengersState = MutableLiveData<ResultState<List<Passenger>>>()
    val pickedUpPassengersState: LiveData<ResultState<List<Passenger>>> get() = _pickedUpPassengersState

    private val _updateStatusState = MutableLiveData<ResultState<Unit>>()
    val updateStatusState: LiveData<ResultState<Unit>> get() = _updateStatusState

    fun fetchPassengers() {
        _waitingPassengersState.value = ResultState.Loading
        _pickedUpPassengersState.value = ResultState.Loading

        viewModelScope.launch {
            // Ambil lokasi pengguna
            val userLocationResult = getUserLocationUseCase()
            val userLat = userLocationResult.getOrNull()?.latitude ?: 0.0
            val userLon = userLocationResult.getOrNull()?.longitude ?: 0.0
            Log.d(TAG, "User location: lat=$userLat, lon=$userLon")

            when (val result = getListPassengersUseCase()) {
                is ResultState.Success -> {
                    val data = result.data.data
                    val waitingPassengers = data?.waiting?.mapNotNull { item ->
                        item?.let { convertToPassenger(it, isWaiting = true, userLat, userLon) }
                    }?.sortedBy { it.distance } ?: emptyList() // Urutkan berdasarkan jarak terdekat
                    _waitingPassengersState.value = ResultState.Success(waitingPassengers)
                    Log.d(TAG, "Waiting passengers loaded: ${waitingPassengers.size}")

                    val pickedUpPassengers = data?.pickedUp?.mapNotNull { item ->
                        item?.let { convertToPassenger(it, isWaiting = false, userLat, userLon) }
                    }?.sortedBy { it.distance } ?: emptyList() // Urutkan berdasarkan jarak terdekat
                    _pickedUpPassengersState.value = ResultState.Success(pickedUpPassengers)
                    Log.d(TAG, "Picked up passengers loaded: ${pickedUpPassengers.size}")
                }
                is ResultState.Error -> {
                    _waitingPassengersState.value = ResultState.Error(result.error)
                    _pickedUpPassengersState.value = ResultState.Error(result.error)
                    Log.e(TAG, "Error fetching passengers: ${result.error}")
                }
                is ResultState.Loading -> {}
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        _updateStatusState.value = ResultState.Loading
        viewModelScope.launch {
            when (val result = updateOrderStatusUseCase(orderId, status)) {
                is ResultState.Success -> {
                    _updateStatusState.value = ResultState.Success(Unit)
                    Log.d(TAG, "Order status updated: orderId=$orderId, status=$status")
                    fetchPassengers() // Refresh data setelah update
                }
                is ResultState.Error -> {
                    _updateStatusState.value = ResultState.Error(result.error)
                    Log.e(TAG, "Error updating order status: ${result.error}")
                }
                is ResultState.Loading -> {}
            }
        }
    }

    private suspend fun convertToPassenger(item: WaitingItemJSON, isWaiting: Boolean, userLat: Double, userLon: Double): Passenger? {
        return try {
            val lat = (if (isWaiting) item.startingPointLat else item.destinationPointLat)?.toDoubleOrNull() ?: return null
            val long = (if (isWaiting) item.startingPointLong else item.destinationPointLong)?.toDoubleOrNull() ?: return null
            Log.d(TAG, "Converting WaitingItem: orderId=${item.orderId}, lat=$lat, long=$long")

            val distance = Utils.calculateDistance(userLat, userLon, lat, long)
            Log.d(TAG, "Distance for orderId=${item.orderId}: $distance km")

            val result = getPlaceNameUseCase(lat, long)
            val placeName = when (result) {
                is ResultState.Success -> {
                    val name = result.data.data?.placeName
                    if (name.isNullOrEmpty()) "Unknown Location" else name
                }
                is ResultState.Error -> "Unknown Location"
                is ResultState.Loading -> "Loading..."
            }

            Passenger(
                orderId = item.orderId ?: return null,
                name = item.passengerName ?: "Unknown",
                phone = item.passengerPhone ?: "Unknown",
                placeName = placeName,
                methodPayment = item.methodPayment ?: "Unknown",
                distance = distance // Tambahkan jarak ke objek Passenger
            )
        } catch (e: Exception) {
            Log.d(TAG, "Error converting WaitingItem orderId=${item.orderId}: ${e.message}")
            Passenger(
                orderId = item.orderId ?: return null,
                name = item.passengerName ?: "Unknown",
                phone = item.passengerPhone ?: "Unknown",
                placeName = "Unknown Location",
                methodPayment = item.methodPayment ?: "Unknown",
                distance = Double.MAX_VALUE // Jarak default jika error
            )
        }
    }

    private suspend fun convertToPassenger(item: PickedUpItemJSON, isWaiting: Boolean, userLat: Double, userLon: Double): Passenger? {
        return try {
            val lat = (if (isWaiting) item.startingPointLat else item.destinationPointLat)?.toDoubleOrNull() ?: return null
            val long = (if (isWaiting) item.startingPointLong else item.destinationPointLong)?.toDoubleOrNull() ?: return null
            Log.d(TAG, "Converting PickedUpItem: orderId=${item.orderId}, lat=$lat, long=$long")

            val distance = Utils.calculateDistance(userLat, userLon, lat, long)
            Log.d(TAG, "Distance for orderId=${item.orderId}: $distance km")

            val result = getPlaceNameUseCase(lat, long)
            val placeName = when (result) {
                is ResultState.Success -> {
                    val name = result.data.data?.placeName
                    if (name.isNullOrEmpty()) "Unknown Location" else name
                }
                is ResultState.Error -> "Unknown Location"
                is ResultState.Loading -> "Loading..."
            }

            Passenger(
                orderId = item.orderId ?: return null,
                name = item.passengerName ?: "Unknown",
                phone = item.passengerPhone ?: "Unknown",
                placeName = placeName,
                methodPayment = item.methodPayment ?: "Unknown",
                distance = distance // Tambahkan jarak ke objek Passenger
            )
        } catch (e: Exception) {
            Log.d(TAG, "Error converting PickedUpItem orderId=${item.orderId}: ${e.message}")
            Passenger(
                orderId = item.orderId ?: return null,
                name = item.passengerName ?: "Unknown",
                phone = item.passengerPhone ?: "Unknown",
                placeName = "Unknown Location",
                methodPayment = item.methodPayment ?: "Unknown",
                distance = Double.MAX_VALUE // Jarak default jika error
            )
        }
    }
}