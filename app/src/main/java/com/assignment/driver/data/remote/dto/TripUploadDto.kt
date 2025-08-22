package com.assignment.driver.data.remote.dto

data class TripUploadDto(
    val tripId: String,
    val driverId: String,
    val routeId: String,
    val startTime: Long,
    val endTime: Long,
    val points: List<TripPointDto>
)

data class TripPointDto(
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val accuracy: Float?,
    val speed: Float?,
    val bearing: Float?
)

