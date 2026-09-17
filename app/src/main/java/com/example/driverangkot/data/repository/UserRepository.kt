package com.example.driverangkot.data.repository

import com.example.driverangkot.data.api.ApiService
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.domain.entity.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(
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
    ): RegisterSuccessResponse {
        fun text(value: String) = value.toRequestBody("text/plain".toMediaType())
        val response = apiService.register(
            text(fullName), text(noHp), text(noHpEmergency), text(email), text(trayekId.toString()),
            text(noPlat), text(password), selfPhoto, ktp, sim, stnk
        )
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respons kosong dari server")
        }
        if (response.code() == 422) {
            val errorJson = Gson().fromJson(response.errorBody()?.string(), JsonObject::class.java)
            val errorMessage = errorJson.get("message")?.asString ?: "Validasi gagal"
            val detailedError = errorJson.getAsJsonObject("errors")?.entrySet()?.joinToString(", ") { entry ->
                entry.value.asJsonArray.joinToString(", ") { it.asString }
            } ?: errorMessage
            throw Exception(detailedError)
        }
        throw HttpException(response)
    }

    suspend fun login(email: String, password: String): LoginSuccessResponse {
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            throw Exception("Email tidak valid")
        }
        if (password.length < 8) {
            throw Exception("Password harus minimal 8 karakter")
        }

        val result = apiService.login(email, password)
        if (result.user?.role != "driver") {
            throw Exception("Hanya driver yang dapat login")
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
        userPreference.saveSession(user, result.token ?: throw Exception("Token tidak ditemukan"))
        return result
    }

    suspend fun logout() = apiService.logout().also { userPreference.logout() }

    suspend fun getSaldo() = apiService.getSaldo()

    suspend fun getHistory() = apiService.getHistory()
}
