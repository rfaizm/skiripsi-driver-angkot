package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class ListPassengerResponse(

	@field:SerializedName("data")
	val data: DataListPassengerJSON? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class WaitingItemJSON(

	@field:SerializedName("number_of_passengers")
	val numberOfPassengers: Int? = null,

	@field:SerializedName("trayek_name")
	val trayekName: String? = null,

	@field:SerializedName("destination_point_lat")
	val destinationPointLat: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("starting_point_lat")
	val startingPointLat: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("passenger_name")
	val passengerName: String? = null,

	@field:SerializedName("passenger_phone")
	val passengerPhone: String? = null,

	@field:SerializedName("starting_point_long")
	val startingPointLong: String? = null,

	@field:SerializedName("destination_point_long")
	val destinationPointLong: String? = null
)

data class PickedUpItemJSON(

	@field:SerializedName("number_of_passengers")
	val numberOfPassengers: Int? = null,

	@field:SerializedName("trayek_name")
	val trayekName: String? = null,

	@field:SerializedName("destination_point_lat")
	val destinationPointLat: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("starting_point_lat")
	val startingPointLat: String? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("passenger_name")
	val passengerName: String? = null,

	@field:SerializedName("passenger_phone")
	val passengerPhone: String? = null,

	@field:SerializedName("starting_point_long")
	val startingPointLong: String? = null,

	@field:SerializedName("destination_point_long")
	val destinationPointLong: String? = null
)

data class DataListPassengerJSON(

	@field:SerializedName("picked_up")
	val pickedUp: List<PickedUpItemJSON?>? = null,

	@field:SerializedName("waiting")
	val waiting: List<WaitingItemJSON?>? = null
)
