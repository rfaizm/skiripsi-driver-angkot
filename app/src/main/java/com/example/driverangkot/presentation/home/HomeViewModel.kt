package com.example.driverangkot.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.di.SingleLiveEvent
import com.example.driverangkot.domain.entity.LatLng
import com.example.driverangkot.domain.entity.OrderData
import com.example.driverangkot.domain.repository.OrderRepository
import com.example.driverangkot.domain.usecase.angkot.ToOfflineUseCase
import com.example.driverangkot.domain.usecase.location.GetUserLocationUseCase
import com.example.driverangkot.domain.usecase.angkot.ToOnlineUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val toOnlineUseCase: ToOnlineUseCase,
    private val toOfflineUseCase: ToOfflineUseCase,
    private val orderRepository: OrderRepository // [Baru]
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _locationState = MutableLiveData<ResultState<LatLng>>()
    val locationState: LiveData<ResultState<LatLng>> get() = _locationState

    private val _toOnlineState = SingleLiveEvent<ResultState<ToOnlineResponse>>()
    val toOnlineState: LiveData<ResultState<ToOnlineResponse>> get() = _toOnlineState

    private val _toOfflineState = SingleLiveEvent<ResultState<ToOfflineResponse>>()
    val toOfflineState: LiveData<ResultState<ToOfflineResponse>> get() = _toOfflineState

    private val _ordersState = MutableLiveData<Map<Int, OrderData>>(emptyMap())
    val ordersState: LiveData<Map<Int, OrderData>> get() = _ordersState


    init {
        // [Baru] Amati data pesanan dari OrderRepository
        viewModelScope.launch {
            orderRepository.getOrders().collectLatest { orders ->
                _ordersState.value = orders.associateBy { it.orderId }
                Log.d(TAG, "Orders updated: ${orders.size} orders")
            }
        }
    }

    fun getUserLocation() {
        _locationState.value = ResultState.Loading
        viewModelScope.launch {
            val result = getUserLocationUseCase()
            _locationState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal mendapatkan lokasi")
            }
        }
    }

    fun toOnline(latitude: Double, longitude: Double) {
        _toOnlineState.value = ResultState.Loading
        viewModelScope.launch {
            val result = toOnlineUseCase(latitude, longitude)
            _toOnlineState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal melakukan online")
            }
        }
    }

    fun toOffline() {
        _toOfflineState.value = ResultState.Loading
        viewModelScope.launch {
            val result = toOfflineUseCase()
            _toOfflineState.value = when {
                result.isSuccess -> ResultState.Success(result.getOrThrow())
                else -> ResultState.Error(result.exceptionOrNull()?.message ?: "Gagal melakukan offline")
            }
        }
    }

    // [Baru] Fungsi untuk menambah atau memperbarui pesanan melalui repository
    suspend fun addOrUpdateOrder(order: OrderData) {
        orderRepository.saveOrder(order)
        Log.d(TAG, "Order added/updated: orderId=${order.orderId}")
    }

    // [Baru] Fungsi untuk menghapus pesanan melalui repository
    suspend fun removeOrder(orderId: Int) {
        orderRepository.removeOrder(orderId)
        Log.d(TAG, "Order removed: orderId=$orderId")
    }
}