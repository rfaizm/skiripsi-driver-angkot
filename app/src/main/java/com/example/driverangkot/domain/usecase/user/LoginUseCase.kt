package com.example.driverangkot.domain.usecase.user

import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.domain.entity.User
import com.example.driverangkot.domain.repository.UserRepository

class LoginUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<LoginSuccessResponse> {
        // Validasi input
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Email tidak valid"))
        }
        if (password.length < 8) {
            return Result.failure(Exception("Password harus minimal 8 karakter"))
        }

        return try {
            val result = userRepository.login(email, password)
            // Validasi role user
            if (result.user?.role != "driver") {
                return Result.failure(Exception("Hanya driver yang dapat login"))
            }

            val user = User(
                id = result.user.id ?: 0,
                email = result.user.email ?: "",
                name = result.user.name ?: "",
                role = result.user.role,
                driverId = result.user.driver?.driverId ?: 0,
                trayekId = result.user.driver?.trayekId ?: 0,
                noHp = result.user.driver?.noHp ?: "",
                noHpEmergency = result.user.driver?.noHpEmergency ?: "",
                platNumber = result.user.driver?.platNomor ?: ""
            )
            userRepository.saveSession(
                user = user,
                token = result.token ?: throw Exception("Token tidak ditemukan"),
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}