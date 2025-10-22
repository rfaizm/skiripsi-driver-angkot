package com.example.driverangkot.data.api


import com.example.driverangkot.data.api.dto.GetPlaceNameResponse
import com.example.driverangkot.data.api.dto.HistoryResponse
import com.example.driverangkot.data.api.dto.ListPassengerResponse
import com.example.driverangkot.data.api.dto.LoginSuccessResponse
import com.example.driverangkot.data.api.dto.LogoutResponse
import com.example.driverangkot.data.api.dto.RegisterSuccessResponse
import com.example.driverangkot.data.api.dto.SaldoDriverResponse
import com.example.driverangkot.data.api.dto.ToOfflineResponse
import com.example.driverangkot.data.api.dto.ToOnlineResponse
import com.example.driverangkot.data.api.dto.UpdateLocationResponse
import com.example.driverangkot.data.api.dto.UpdateOrderStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("register/driver")
    suspend fun register(
        @Part("full_name") fullName: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part("no_hp_emergency") noHpEmergency: RequestBody,
        @Part("email") email: RequestBody,
        @Part("trayek_id") trayekId: RequestBody,
        @Part("no_plat") noPlat: RequestBody,
        @Part("password") password: RequestBody,
        @Part selfPhoto: MultipartBody.Part,
        @Part ktp: MultipartBody.Part,
        @Part sim: MultipartBody.Part,
        @Part stnk: MultipartBody.Part
    ): Response<RegisterSuccessResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email : String,
        @Field("password") password : String
    ) : LoginSuccessResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): LogoutResponse

    @FormUrlEncoded
    @PATCH("to-online")
    suspend fun toOnline(
        @Header("Authorization") token: String,
        @Field("lat") latitude: Double,
        @Field("long") longitude: Double
    ) : ToOnlineResponse

    @PATCH("to-offline")
    suspend fun toOffline(
        @Header("Authorization") token: String,
    ) : ToOfflineResponse

    @FormUrlEncoded
    @POST("angkot/update-location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Field("lat") latitude: Double,
        @Field("long") longitude: Double
    ) : UpdateLocationResponse

    @GET("driver/orders")
    suspend fun getListPassengers(
        @Header("Authorization") token: String,
    ) : ListPassengerResponse

    @FormUrlEncoded
    @POST("place-name")
    suspend fun getPlaceName(
        @Header("Authorization") token: String,
        @Field("lat") latitude: Double,
        @Field("long") longitude: Double
    ) : GetPlaceNameResponse

    @FormUrlEncoded
    @PATCH("update-order-status")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Field("order_id") orderId: String,
        @Field("status") status: String
    ) : UpdateOrderStatusResponse

    @GET("driver/saldo")
    suspend fun getSaldo(
        @Header("Authorization") token: String,
    ) : SaldoDriverResponse

    @GET("driver/history")
    suspend fun getHistory(
        @Header("Authorization") token: String,
    ) : HistoryResponse
}
