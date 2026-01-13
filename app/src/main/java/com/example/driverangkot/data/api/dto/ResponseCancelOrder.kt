package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class ResponseCancelOrder(

	@field:SerializedName("data")
	val data: CancelOrderData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CancelOrderData(

	@field:SerializedName("returned_amount")
	val returnedAmount: Int? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
