package com.example.driverangkot.di

import android.content.Context
import com.example.driverangkot.data.api.ApiConfig
import com.example.driverangkot.data.datasource.AngkotDataSourceImpl
import com.example.driverangkot.data.datasource.ListPassengersDataSourceImpl
import com.example.driverangkot.data.datasource.LocationDataSourceImpl
import com.example.driverangkot.data.datasource.OrderDataSourceImpl
import com.example.driverangkot.data.datasource.UserDataSourceImpl
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
import com.example.driverangkot.domain.usecase.user.GetDriverSaldoUseCase
import com.example.driverangkot.domain.usecase.user.GetHistoryUseCase

object Injection {
    private fun provideUserRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val userDataSource = UserDataSourceImpl(apiService, userPreference)
        return UserRepositoryImpl(userDataSource)
    }

    private fun provideLocationRepository(context: Context): LocationRepository {
        val locationDataSource = LocationDataSourceImpl(context)
        return LocationRepositoryImpl(locationDataSource)
    }

    private fun provideAngkotRepository(context: Context): AngkotRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val angkotDataSource = AngkotDataSourceImpl(apiService, userPreference)
        return AngkotRepositoryImpl(angkotDataSource)
    }

    fun provideOrderRepository(context: Context): OrderRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val orderDataSource = OrderDataSourceImpl(context.dataStore, context, apiService, userPreference)
        return OrderRepositoryImpl(orderDataSource)
    }

    private fun provideListPassengersRepository(context: Context): ListPassengersRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(context.dataStore)
        val listPassengersDataSource = ListPassengersDataSourceImpl(apiService, userPreference)
        return ListPassengersRepositoryImpl(listPassengersDataSource)
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

    fun provideGetListPassengersUseCase(context: Context): GetListPassengersUseCase {
        return GetListPassengersUseCase(provideListPassengersRepository(context))
    }

    fun provideGetPlaceNameUseCase(context: Context): GetPlaceNameUseCase {
        return GetPlaceNameUseCase(provideListPassengersRepository(context))
    }

    fun provideUpdateOrderStatusUseCase(context: Context): UpdateOrderStatusUseCase {
        return UpdateOrderStatusUseCase(provideOrderRepository(context))
    }

    fun provideGetDriverSaldoUseCase(context: Context): GetDriverSaldoUseCase {
        return GetDriverSaldoUseCase(provideUserRepository(context))
    }

    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase {
        return GetHistoryUseCase(provideUserRepository(context))
    }
}