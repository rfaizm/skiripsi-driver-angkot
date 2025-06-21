package com.example.driverangkot.di

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val error: String) : ResultState<Nothing>()
}