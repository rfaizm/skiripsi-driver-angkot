package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class UpdateOrderStatusResponse(

	@field:SerializedName("data")
	val data: DataUpdateOrderStatusJSON? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataUpdateOrderStatusJSON(

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
