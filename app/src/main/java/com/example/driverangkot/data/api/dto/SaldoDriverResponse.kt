package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class SaldoDriverResponse(

	@field:SerializedName("data")
	val data: SaldoData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class SaldoData(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("daily")
	val daily: Int? = null,

	@field:SerializedName("weekly")
	val weekly: Int? = null
)
