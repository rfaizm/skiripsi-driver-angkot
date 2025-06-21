package com.example.driverangkot.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.driverangkot.domain.repository.OrderRepository
import com.example.driverangkot.domain.usecase.angkot.ToOfflineUseCase
import com.example.driverangkot.domain.usecase.location.GetUserLocationUseCase
import com.example.driverangkot.domain.usecase.user.LoginUseCase
import com.example.driverangkot.domain.usecase.user.LogoutUseCase
import com.example.driverangkot.domain.usecase.user.RegisterUseCase
import com.example.driverangkot.domain.usecase.angkot.ToOnlineUseCase
import com.example.driverangkot.domain.usecase.listpassenger.GetListPassengersUseCase
import com.example.driverangkot.domain.usecase.listpassenger.GetPlaceNameUseCase
import com.example.driverangkot.domain.usecase.order.UpdateOrderStatusUseCase
import com.example.driverangkot.presentation.home.HomeViewModel
import com.example.driverangkot.presentation.listpassenger.ListPassengersViewModel
import com.example.driverangkot.presentation.login.LoginViewModel
import com.example.driverangkot.presentation.profile.ProfileViewModel
import com.example.driverangkot.presentation.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val toOnlineUseCase: ToOnlineUseCase,
    private val toOfflineUseCase: ToOfflineUseCase,
    private val orderRepository: OrderRepository,
    private val getListPassengersUseCase: GetListPassengersUseCase, // [Baru]
    private val getPlaceNameUseCase: GetPlaceNameUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(registerUseCase) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginUseCase) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(logoutUseCase) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(getUserLocationUseCase, toOnlineUseCase, toOfflineUseCase, orderRepository) as T
            }
            modelClass.isAssignableFrom(ListPassengersViewModel::class.java) -> {
                ListPassengersViewModel(getListPassengersUseCase, getPlaceNameUseCase, updateOrderStatusUseCase) as T // [Berubah]
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRegisterUseCase(context),
                    Injection.provideLoginUseCase(context),
                    Injection.provideLogoutUseCase(context),
                    Injection.provideGetUserLocationUseCase(context),
                    Injection.provideToOnlineUseCase(context),
                    Injection.provideToOfflineUseCase(context),
                    Injection.provideOrderRepositoryUseCase(context),
                    Injection.provideGetListPassengersUseCase(context), // [Baru]
                    Injection.provideGetPlaceNameUseCase(context),
                    Injection.provideUpdateOrderStatusUseCase(context)
                )
            }.also { instance = it }
    }
}