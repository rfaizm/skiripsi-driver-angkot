package com.example.driverangkot.domain.usecase.user

import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.domain.repository.UserRepository

class LogoutUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<LogoutResponse> {
        return try {
            val response = userRepository.logout()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}