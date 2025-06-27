package com.example.driverangkot.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.data.datasource.OrderDataSource
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
    private val orderDataSource: OrderDataSource
) : OrderRepository {

    private val TAG = "OrderRepositoryImpl"

    override suspend fun saveOrder(order: OrderData) {
        try {
            Log.d(TAG, "Saving order: orderId=${order.orderId}")
            orderDataSource.saveOrder(order)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving order: ${e.message}", e)
            throw e
        }
    }

    override suspend fun removeOrder(orderId: Int) {
        try {
            Log.d(TAG, "Removing order: orderId=$orderId")
            orderDataSource.removeOrder(orderId)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing order: ${e.message}", e)
            throw e
        }
    }

    override fun getOrders(): Flow<List<OrderData>> {
        return orderDataSource.getOrders()
    }

    override suspend fun updateOrderStatus(orderId: Int, status: String): UpdateOrderStatusResponse {
        try {
            Log.d(TAG, "Updating order status: orderId=$orderId, status=$status")
            val response = orderDataSource.updateOrderStatus(orderId, status)
            if (status == "selesai") {
                removeOrder(orderId)
            }
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status: ${e.message}", e)
            throw e
        }
    }
}