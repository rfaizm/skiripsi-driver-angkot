package com.example.driverangkot.domain.usecase.user

import com.example.driverangkot.data.api.dto.HistoryResponse
import com.example.driverangkot.domain.repository.UserRepository

class GetHistoryUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<HistoryResponse> {
        return try {
            Result.success(userRepository.getHistory())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}