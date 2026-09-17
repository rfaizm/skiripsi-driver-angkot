package com.example.driverangkot.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.ResponseCancelOrder
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.domain.entity.OrderData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepository(
    private val dataStore: DataStore<Preferences>,
    private val apiService: ApiService
) {

    private val ordersKey = stringPreferencesKey("orders")
    private val gson = Gson()

    private fun Preferences.orders(): List<OrderData> =
        gson.fromJson(this[ordersKey] ?: "[]", object : TypeToken<List<OrderData>>() {}.type) ?: emptyList()

    private fun MutablePreferences.setOrders(orders: List<OrderData>) {
        this[ordersKey] = gson.toJson(orders)
    }

    suspend fun saveOrder(order: OrderData) {
        dataStore.edit { preferences ->
            val orders = preferences.orders()
            preferences.setOrders(
                if (orders.any { it.orderId == order.orderId }) orders.map { if (it.orderId == order.orderId) order else it }
                else orders + order
            )
        }
    }

    suspend fun removeOrder(orderId: Int) {
        dataStore.edit { preferences ->
            preferences.setOrders(preferences.orders().filter { it.orderId != orderId })
        }
    }

    fun getOrders(): Flow<List<OrderData>> = dataStore.data.map { it.orders() }

    suspend fun updateOrderStatus(orderId: Int, status: String): UpdateOrderStatusResponse {
        val response = apiService.updateOrderStatus(orderId.toString(), status)
        if (status == "selesai") removeOrder(orderId)
        return response
    }

    suspend fun cancelOrder(orderId: Int): ResponseCancelOrder {
        val response = apiService.cancelOrder(orderId.toString())
        if (!response.isSuccessful) {
            val errorJson = gson.fromJson(response.errorBody()?.string(), JsonObject::class.java)
            val errorMessage = errorJson?.get("message")?.asString ?: "Terjadi kesalahan saat membatalkan pesanan"
            throw Exception(errorJson?.get("error")?.asString ?: errorMessage)
        }
        val body = response.body() ?: throw Exception("Respons kosong dari server")
        removeOrder(orderId)
        return body
    }

    suspend fun getListPassengers() = apiService.getListPassengers()

    suspend fun getPlaceName(latitude: Double, longitude: Double) = apiService.getPlaceName(latitude, longitude)
}
