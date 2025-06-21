package com.example.driverangkot.data.repository

import android.util.Log
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.DescRegisterJSON
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.entity.User
import com.example.driverangkot.domain.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : UserRepository {

    override suspend fun register(
        fullName: RequestBody,
        noHp: RequestBody,
        noHpEmergency: RequestBody,
        email: RequestBody,
        trayekId: RequestBody,
        noPlat: RequestBody,
        password: RequestBody,
        selfPhoto: MultipartBody.Part,
        ktp: MultipartBody.Part,
        sim: MultipartBody.Part,
        stnk: MultipartBody.Part
    ): RegisterSuccessResponse {
        return apiService.register(
            fullName, noHp, noHpEmergency, email, trayekId, noPlat, password,
            selfPhoto, ktp, sim, stnk
        )
    }

    override suspend fun login(email: String, password: String): LoginSuccessResponse {
        try {
            val response = apiService.login(email, password)
            Log.d("UserRepositoryImpl", "Response: $response")

            return response
        } catch (e: HttpException) {
            throw Exception("Gagal login: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSession(user: User, token: String) {
        userPreference.saveSession(user, token)
    }

    override suspend fun logout(): LogoutResponse {
        try {
            val token = userPreference.getAuthToken()
                ?: throw Exception("Token tidak ditemukan")
            val response = apiService.logout("Bearer $token")
            userPreference.logout() // Hapus semua preferensi
            return response
        } catch (e: HttpException) {
            throw Exception("Gagal logout: ${e.message()}")
        } catch (e: Exception) {
            throw e
        }
    }
}