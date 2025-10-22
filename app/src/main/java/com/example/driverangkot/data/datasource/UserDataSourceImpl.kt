package com.example.driverangkot.data.datasource

import android.util.Log
import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.HistoryResponse
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.data.api.dto.SaldoDriverResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.entity.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserDataSourceImpl(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : UserDataSource {

    private val TAG = "UserDataSourceImpl"

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
        try {
            Log.d(TAG, "Calling register")
            val response = apiService.register(fullName, noHp, noHpEmergency, email, trayekId, noPlat, password, selfPhoto, ktp, sim, stnk)
            if (response.isSuccessful) {
                return response.body() ?: throw Exception("Respons kosong dari server")
            } else {
                // [Baru] Tangani error 422
                if (response.code() == 422) {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
                    val errorMessage = errorJson.get("message")?.asString ?: "Validasi gagal"
                    val errors = errorJson.getAsJsonObject("errors")
                    val detailedError = errors?.entrySet()?.joinToString(", ") { entry ->
                        entry.value.asJsonArray.joinToString(", ") { it.asString }
                    } ?: errorMessage
                    throw Exception(detailedError)
                }
                throw HttpException(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in register: ${e.message}", e)
            throw e
        }
    }

    override suspend fun login(email: String, password: String): LoginSuccessResponse {
        try {
            Log.d(TAG, "Calling login with email=$email")
            val response = apiService.login(email, password)
            Log.d(TAG, "Login response: $response")
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error in login: ${e.message}", e)
            throw e
        }
    }

    override suspend fun saveSession(user: User, token: String) {
        try {
            Log.d(TAG, "Saving session for user: ${user.name}")
            userPreference.saveSession(user, token)
        } catch (e: Exception) {
            Log.e(TAG, "Error in saveSession: ${e.message}", e)
            throw e
        }
    }

    override suspend fun logout(): LogoutResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Calling logout with token: Bearer $token")
            val response = apiService.logout("Bearer $token")
            userPreference.logout()
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error in logout: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getSaldo(): SaldoDriverResponse {
        try {
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            Log.d(TAG, "Fetching saldo with token: Bearer $token")
            val response = apiService.getSaldo("Bearer $token")
            Log.d(TAG, "Saldo response: $response")
            return response
        } catch (e: Exception) {
            Log.e(TAG, "Error in getSaldo: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getHistory(): HistoryResponse {
        try {
            Log.d(TAG, "Fetching history")
            val token = userPreference.getAuthToken() ?: throw Exception("Token tidak ditemukan")
            return apiService.getHistory("Bearer $token")
        } catch (e: Exception) {
            Log.e(TAG, "Error in getHistory: ${e.message}", e)
            throw e
        }
    }
}