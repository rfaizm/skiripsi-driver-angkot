package com.example.driverangkot.domain.usecase.user

import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        fullName: String,
        noHp: String,
        noHpEmergency: String,
        email: String,
        trayekId: Int,
        noPlat: String,
        password: String,
        selfPhoto: MultipartBody.Part,
        ktp: MultipartBody.Part,
        sim: MultipartBody.Part,
        stnk: MultipartBody.Part
    ): Result<RegisterSuccessResponse> {
        return try {
            val fullNameBody = fullName.toRequestBody("text/plain".toMediaType())
            val noHpBody = noHp.toRequestBody("text/plain".toMediaType())
            val noHpEmergencyBody = noHpEmergency.toRequestBody("text/plain".toMediaType())
            val emailBody = email.toRequestBody("text/plain".toMediaType())
            val trayekIdBody = trayekId.toString().toRequestBody("text/plain".toMediaType())
            val noPlatBody = noPlat.toRequestBody("text/plain".toMediaType())
            val passwordBody = password.toRequestBody("text/plain".toMediaType())

            val response = userRepository.register(
                fullNameBody, noHpBody, noHpEmergencyBody, emailBody, trayekIdBody, noPlatBody, passwordBody,
                selfPhoto, ktp, sim, stnk
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}