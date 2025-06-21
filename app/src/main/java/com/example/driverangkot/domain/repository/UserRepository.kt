package com.example.driverangkot.domain.repository

import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.domain.entity.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun register(
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
    ): RegisterSuccessResponse
    suspend fun login(email: String, password: String): LoginSuccessResponse
    suspend fun saveSession(user: User, token: String)
    suspend fun logout(): LogoutResponse

}