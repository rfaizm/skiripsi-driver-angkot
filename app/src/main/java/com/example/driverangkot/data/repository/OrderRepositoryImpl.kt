package com.example.driverangkot.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.entity.OrderData
import com.example.driverangkot.domain.repository.OrderRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class OrderRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val context: Context,
    private val apiService: ApiService, // [Baru] Tambahkan ApiService untuk API call
    private val userPreference: UserPreference // [Baru] Untuk token
) : OrderRepository {

    private val ORDERS_KEY = stringPreferencesKey("orders")
    private val gson = Gson()
    private val TAG = "OrderRepositoryImpl"

    override suspend fun saveOrder(order: OrderData) {
        dataStore.edit { preferences ->
            val currentOrders = getOrdersSync().toMutableList()
            val existingIndex = currentOrders.indexOfFirst { it.orderId == order.orderId }
            if (existingIndex != -1) {
                currentOrders[existingIndex] = order
            } else {
                currentOrders.add(order)
            }
            preferences[ORDERS_KEY] = gson.toJson(currentOrders)
        }
        Log.d(TAG, "Order saved: orderId=${order.orderId}")
    }

    override suspend fun removeOrder(orderId: Int) {
        dataStore.edit { preferences ->
            val currentOrders = getOrdersSync().toMutableList()
            currentOrders.removeAll { it.orderId == orderId }
            preferences[ORDERS_KEY] = gson.toJson(currentOrders)
        }
        Log.d(TAG, "Order removed: orderId=$orderId")
    }

    override fun getOrders(): Flow<List<OrderData>> {
        return dataStore.data.map { preferences ->
            val ordersJson = preferences[ORDERS_KEY] ?: "[]"
            val type = object : TypeToken<List<OrderData>>() {}.type
            gson.fromJson(ordersJson, type) ?: emptyList()
        }
    }

    private fun getOrdersSync(): List<OrderData> {
        val ordersJson = runBlocking {
            dataStore.data.first()[ORDERS_KEY] ?: "[]"
        }
        val type = object : TypeToken<List<OrderData>>() {}.type
        return gson.fromJson(ordersJson, type) ?: emptyList()
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): UpdateOrderStatusResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Updating order status: orderId=$orderId, status=$status, token=Bearer $token")
            val response = apiService.updateOrderStatus("Bearer $token", orderId.toString(), status)
            Log.d(TAG, "Update order status response: $response")
            // Hapus order dari DataStore jika status adalah "selesai"
            if (status == "selesai") {
                removeOrder(orderId)
            }
            return response
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error updating order status: ${e.message()}", e)
            throw Exception("Gagal memperbarui status pesanan: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status: ${e.message}", e)
            throw e
        }
    }
}