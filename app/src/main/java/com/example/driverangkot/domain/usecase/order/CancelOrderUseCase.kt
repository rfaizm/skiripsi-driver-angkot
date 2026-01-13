package com.example.driverangkot.domain.usecase.order

import com.example.driverangkot.data.api.dto.ResponseCancelOrder
import com.example.driverangkot.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CancelOrderUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(orderId: Int): Result<ResponseCancelOrder> {
        return try {
            withContext(Dispatchers.IO) {
                val response = orderRepository.cancelOrder(orderId)
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}