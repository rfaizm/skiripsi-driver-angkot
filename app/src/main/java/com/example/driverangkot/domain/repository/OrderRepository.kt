package com.example.driverangkot.domain.repository

import com.example.driverangkot.data.api.dto.ResponseCancelOrder
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.domain.entity.OrderData
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun saveOrder(order: OrderData)
    suspend fun removeOrder(orderId: Int)
    fun getOrders(): Flow<List<OrderData>>
    suspend fun updateOrderStatus(orderId: Int, status: String): UpdateOrderStatusResponse // [Baru]
    suspend fun cancelOrder(orderId: Int): ResponseCancelOrder // [Baru]
}