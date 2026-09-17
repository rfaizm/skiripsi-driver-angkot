package com.example.driverangkot.presentation.listpassenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.ResponseCancelOrder
import com.example.driverangkot.data.repository.LocationRepository
import com.example.driverangkot.data.repository.OrderRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.toResultState
import com.example.driverangkot.domain.entity.Passenger
import com.example.driverangkot.utils.Utils
import kotlinx.coroutines.launch

class ListPassengersViewModel(
    private val orderRepository: OrderRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _waitingPassengersState = MutableLiveData<ResultState<List<Passenger>>>()
    val waitingPassengersState: LiveData<ResultState<List<Passenger>>> get() = _waitingPassengersState

    private val _pickedUpPassengersState = MutableLiveData<ResultState<List<Passenger>>>()
    val pickedUpPassengersState: LiveData<ResultState<List<Passenger>>> get() = _pickedUpPassengersState

    private val _updateStatusState = MutableLiveData<ResultState<Unit>>()
    val updateStatusState: LiveData<ResultState<Unit>> get() = _updateStatusState

    private val _cancelOrderState = MutableLiveData<ResultState<ResponseCancelOrder>>()
    val cancelOrderState: LiveData<ResultState<ResponseCancelOrder>> get() = _cancelOrderState

    fun fetchPassengers() {
        _waitingPassengersState.value = ResultState.Loading
        _pickedUpPassengersState.value = ResultState.Loading

        viewModelScope.launch {
            val userLocation = locationRepository.getLastLocation()
            val userLat = userLocation?.latitude ?: 0.0
            val userLon = userLocation?.longitude ?: 0.0

            runCatching { orderRepository.getListPassengers() }
                .onSuccess { response ->
                    val data = response.data
                    _waitingPassengersState.value = ResultState.Success(
                        data?.waiting.orEmpty().filterNotNull().mapNotNull {
                            toPassenger(it.orderId, it.passengerName, it.passengerPhone, it.methodPayment,
                                it.startingPointLat, it.startingPointLong, userLat, userLon)
                        }.sortedBy { it.distance }
                    )
                    _pickedUpPassengersState.value = ResultState.Success(
                        data?.pickedUp.orEmpty().filterNotNull().mapNotNull {
                            toPassenger(it.orderId, it.passengerName, it.passengerPhone, it.methodPayment,
                                it.destinationPointLat, it.destinationPointLong, userLat, userLon)
                        }.sortedBy { it.distance }
                    )
                }
                .onFailure {
                    val error = ResultState.Error(it.message ?: "Gagal mendapatkan daftar penumpang")
                    _waitingPassengersState.value = error
                    _pickedUpPassengersState.value = error
                }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        _updateStatusState.value = ResultState.Loading
        viewModelScope.launch {
            val result = runCatching { orderRepository.updateOrderStatus(orderId, status) }
            _updateStatusState.value = result.map { }.toResultState("Gagal memperbarui status pesanan")
            if (result.isSuccess) fetchPassengers()
        }
    }

    fun cancelOrder(orderId: Int) {
        _cancelOrderState.value = ResultState.Loading
        viewModelScope.launch {
            val result = runCatching { orderRepository.cancelOrder(orderId) }
            _cancelOrderState.value = result.toResultState("Gagal membatalkan pesanan")
            if (result.isSuccess) fetchPassengers()
        }
    }

    private suspend fun toPassenger(
        orderId: Int?, name: String?, phone: String?, methodPayment: String?,
        latText: String?, longText: String?, userLat: Double, userLon: Double
    ): Passenger? {
        val lat = latText?.toDoubleOrNull() ?: return null
        val long = longText?.toDoubleOrNull() ?: return null
        val placeName = runCatching { orderRepository.getPlaceName(lat, long) }
            .getOrNull()?.data?.placeName
        return Passenger(
            orderId = orderId ?: return null,
            name = name ?: "Unknown",
            phone = phone ?: "Unknown",
            placeName = if (placeName.isNullOrEmpty()) "Unknown Location" else placeName,
            methodPayment = methodPayment ?: "Unknown",
            distance = Utils.calculateDistance(userLat, userLon, lat, long)
        )
    }
}
