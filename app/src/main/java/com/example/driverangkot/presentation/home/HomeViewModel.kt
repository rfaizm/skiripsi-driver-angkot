package com.example.driverangkot.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.repository.AngkotRepository
import com.example.driverangkot.data.repository.LocationRepository
import com.example.driverangkot.data.repository.OrderRepository
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.SingleLiveEvent
import com.example.driverangkot.di.toResultState
import com.example.driverangkot.domain.entity.OrderData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationRepository: LocationRepository,
    private val angkotRepository: AngkotRepository,
    orderRepository: OrderRepository
) : ViewModel() {

    private val _locationState = MutableLiveData<ResultState<LatLng>>()
    val locationState: LiveData<ResultState<LatLng>> get() = _locationState

    private val _latestLocation = MutableLiveData<LatLng>()
    val latestLocation: LiveData<LatLng> get() = _latestLocation

    private val _toOnlineState = SingleLiveEvent<ResultState<ToOnlineResponse>>()
    val toOnlineState: LiveData<ResultState<ToOnlineResponse>> get() = _toOnlineState

    private val _toOfflineState = SingleLiveEvent<ResultState<ToOfflineResponse>>()
    val toOfflineState: LiveData<ResultState<ToOfflineResponse>> get() = _toOfflineState

    private val _ordersState = MutableLiveData<Map<Int, OrderData>>(emptyMap())
    val ordersState: LiveData<Map<Int, OrderData>> get() = _ordersState

    init {
        viewModelScope.launch {
            orderRepository.getOrders().collectLatest { orders ->
                _ordersState.value = orders.associateBy { it.orderId }
            }
        }
    }

    fun updateLatestLocation(lat: Double, lng: Double) {
        _latestLocation.value = LatLng(lat, lng)
    }

    fun getUserLocation() {
        _locationState.value = ResultState.Loading
        viewModelScope.launch {
            val location = locationRepository.getLastLocation()
            _locationState.value = if (location != null) ResultState.Success(location)
            else ResultState.Error("Tidak dapat mendapatkan lokasi")
        }
    }

    fun toOnline(latitude: Double, longitude: Double) {
        _toOnlineState.value = ResultState.Loading
        viewModelScope.launch {
            _toOnlineState.value = runCatching { angkotRepository.toOnline(latitude, longitude) }
                .toResultState("Gagal melakukan online")
        }
    }

    fun toOffline() {
        _toOfflineState.value = ResultState.Loading
        viewModelScope.launch {
            _toOfflineState.value = runCatching { angkotRepository.toOffline() }
                .toResultState("Gagal melakukan offline")
        }
    }
}
