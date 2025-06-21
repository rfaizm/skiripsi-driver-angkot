package com.example.driverangkot.domain.entity

data class User(
    val id : Int,
    val driverId : Int,
    val trayekId : Int,
    val email : String,
    val name : String,
    val noHp : String,
    val noHpEmergency : String,
    val platNumber: String,
    val role : String,
)