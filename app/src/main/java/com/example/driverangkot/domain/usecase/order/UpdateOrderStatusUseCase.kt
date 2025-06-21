package com.example.driverangkot.domain.usecase.order

import android.util.Log
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import com.example.driverangkot.di.ResultState
import com.example.driverangkot.domain.repository.OrderRepository

class UpdateOrderStatusUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: Int, status: String): ResultState<UpdateOrderStatusResponse> {
        return try {
            Log.d("UpdateOrderStatusUseCase", "Menjalankan updateOrderStatusUseCase: orderId=$orderId, status=$status")
            val response = orderRepository.updateOrderStatus(orderId, status)
            Log.d("UpdateOrderStatusUseCase", "Response: $response")
            ResultState.Success(response)
        } catch (e: Exception) {
            Log.e("UpdateOrderStatusUseCase", "Error: ${e.message}", e)
            ResultState.Error(e.message ?: "Gagal memperbarui status pesanan")
        }
    }
}