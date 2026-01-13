package com.example.driverangkot.data.datasource

import com.example.driverangkot.data.api.dto.ResponseCancelOrder
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.domain.entity.OrderData
import kotlinx.coroutines.flow.Flow

interface OrderDataSource {
    suspend fun saveOrder(order: OrderData)
    suspend fun removeOrder(orderId: Int)
    fun getOrders(): Flow<List<OrderData>>
    suspend fun updateOrderStatus(orderId: Int, status: String): UpdateOrderStatusResponse

    suspend fun cancelOrder(orderId: Int) : ResponseCancelOrder
}