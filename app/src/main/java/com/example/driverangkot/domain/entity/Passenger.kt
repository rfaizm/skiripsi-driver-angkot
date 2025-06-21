package com.example.driverangkot.domain.entity

data class Passenger(
    val orderId: Int,
    val name: String,
    val phone: String,
    val placeName: String,
    val isDone: Boolean = false // Placeholder untuk SlideToActView
)
