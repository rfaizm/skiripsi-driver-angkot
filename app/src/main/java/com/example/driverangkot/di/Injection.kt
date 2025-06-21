package com.example.driverangkot.di

import android.content.Context
import com.example.driverangkot.data.api.ApiConfig
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.data.repository.AngkotRepositoryImpl
import com.example.driverangkot.data.repository.ListPassengersRepositoryImpl
import com.example.driverangkot.data.repository.LocationRepositoryImpl
import com.example.driverangkot.data.repository.OrderRepositoryImpl
import com.example.driverangkot.data.repository.UserRepositoryImpl
import com.example.driverangkot.domain.repository.AngkotRepository
import com.example.driverangkot.domain.repository.ListPassengersRepository
import com.example.driverangkot.domain.repository.LocationRepository
import com.example.driverangkot.domain.repository.OrderRepository
import com.example.driverangkot.domain.repository.UserRepository
import com.example.driverangkot.domain.usecase.angkot.ToOfflineUseCase
import com.example.driverangkot.domain.usecase.location.GetUserLocationUseCase
import com.example.driverangkot.domain.usecase.user.LoginUseCase
import com.example.driverangkot.domain.usecase.user.LogoutUseCase
import com.example.driverangkot.domain.usecase.user.RegisterUseCase
import com.example.driverangkot.domain.usecase.angkot.ToOnlineUseCase
import com.example.driverangkot.domain.usecase.angkot.UpdateLocationUseCase
import com.example.driverangkot.domain.usecase.listpassenger.GetListPassengersUseCase
import com.example.driverangkot.domain.usecase.listpassenger.GetPlaceNameUseCase
import com.example.driverangkot.domain.usecase.order.UpdateOrderStatusUseCase

object Injection {
    private fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepositoryImpl(apiService, userPreference)
    }

    private fun provideLocationRepository(context: Context): LocationRepository {
        return LocationRepositoryImpl(context)
    }

    private fun provideAngkotRepository(context: Context): AngkotRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return AngkotRepositoryImpl(apiService, userPreference)
    }

    fun provideOrderRepository(context: Context): OrderRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return OrderRepositoryImpl(context.dataStore, context, apiService, userPreference)
    }

    // [Baru] Menyediakan ListPassengersRepository
    private fun provideListPassengersRepository(context: Context): ListPassengersRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        return ListPassengersRepositoryImpl(apiService, userPreference)
    }

    fun provideRegisterUseCase(context: Context): RegisterUseCase {
        return RegisterUseCase(provideUserRepository(context))
    }

    fun provideLoginUseCase(context: Context): LoginUseCase {
        return LoginUseCase(provideUserRepository(context))
    }

    fun provideLogoutUseCase(context: Context): LogoutUseCase {
        return LogoutUseCase(provideUserRepository(context))
    }

    fun provideGetUserLocationUseCase(context: Context): GetUserLocationUseCase {
        return GetUserLocationUseCase(provideLocationRepository(context))
    }

    fun provideToOnlineUseCase(context: Context): ToOnlineUseCase {
        return ToOnlineUseCase(provideAngkotRepository(context))
    }

    fun provideToOfflineUseCase(context: Context): ToOfflineUseCase {
        return ToOfflineUseCase(provideAngkotRepository(context))
    }

    fun provideUpdateLocationUseCase(context: Context): UpdateLocationUseCase {
        return UpdateLocationUseCase(provideAngkotRepository(context))
    }

    fun provideOrderRepositoryUseCase(context: Context): OrderRepository {
        return provideOrderRepository(context)
    }

    //  Menyediakan GetListPassengersUseCase
    fun provideGetListPassengersUseCase(context: Context): GetListPassengersUseCase {
        return GetListPassengersUseCase(provideListPassengersRepository(context))
    }

    //  Menyediakan GetPlaceNameUseCase
    fun provideGetPlaceNameUseCase(context: Context): GetPlaceNameUseCase {
        return GetPlaceNameUseCase(provideListPassengersRepository(context))
    }

    fun provideUpdateOrderStatusUseCase(context: Context): UpdateOrderStatusUseCase {
        return UpdateOrderStatusUseCase(provideOrderRepository(context))
    }
}