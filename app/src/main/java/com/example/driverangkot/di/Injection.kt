package com.example.driverangkot.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.driverangkot.data.api.ApiConfig
import com.example.driverangkot.data.preference.UserPreference
import com.example.driverangkot.data.preference.dataStore
import com.example.driverangkot.data.repository.AngkotRepository
import com.example.driverangkot.data.repository.LocationRepository
import com.example.driverangkot.data.repository.OrderRepository
import com.example.driverangkot.data.repository.UserRepository
import com.example.driverangkot.presentation.home.HomeViewModel
import com.example.driverangkot.presentation.income.IncomeViewModel
import com.example.driverangkot.presentation.listpassenger.ListPassengersViewModel
import com.example.driverangkot.presentation.login.LoginViewModel
import com.example.driverangkot.presentation.profile.ProfileViewModel
import com.example.driverangkot.presentation.register.RegisterViewModel

object Injection {
    private fun userPreference(context: Context) = UserPreference.getInstance(context.dataStore)
    private fun apiService(context: Context) = ApiConfig.getApiService(userPreference(context))

    fun userRepository(context: Context) = UserRepository(apiService(context), userPreference(context))
    fun angkotRepository(context: Context) = AngkotRepository(apiService(context))
    fun orderRepository(context: Context) = OrderRepository(context.dataStore, apiService(context))
    fun locationRepository(context: Context) = LocationRepository(context)

    val viewModelFactory = viewModelFactory {
        initializer { LoginViewModel(userRepository(this[APPLICATION_KEY]!!)) }
        initializer { RegisterViewModel(userRepository(this[APPLICATION_KEY]!!)) }
        initializer { ProfileViewModel(userRepository(this[APPLICATION_KEY]!!)) }
        initializer { IncomeViewModel(userRepository(this[APPLICATION_KEY]!!)) }
        initializer {
            val app = this[APPLICATION_KEY]!!
            HomeViewModel(locationRepository(app), angkotRepository(app), orderRepository(app))
        }
        initializer {
            val app = this[APPLICATION_KEY]!!
            ListPassengersViewModel(orderRepository(app), locationRepository(app))
        }
    }
}
