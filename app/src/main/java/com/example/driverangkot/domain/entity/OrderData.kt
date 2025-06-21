package com.example.driverangkot.domain.entity

data class OrderData(
    val orderId: Int,
    val startLat: Double,
    val startLong: Double,
    val destLat: Double,
    val destLong: Double,
    val passengers: Int,
    val price: Int,
    val trayekName: String,
    val status: String = "menunggu" // Status awal pesanan
)