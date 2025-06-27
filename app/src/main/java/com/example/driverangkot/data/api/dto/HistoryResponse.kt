package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<DataItemHistory?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItemHistory(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("order_number")
	val orderNumber: String? = null,

	@field:SerializedName("price_total")
	val priceTotal: Int? = null
)
