package com.example.driverangkot.domain.entity

data class Passenger(
    val orderId: Int,
    val name: String,
    val phone: String,
    val placeName: String,
    val methodPayment: String,
    val isDone: Boolean = false,
    val distance: Double = Double.MAX_VALUE // Tambahkan properti distance
)
