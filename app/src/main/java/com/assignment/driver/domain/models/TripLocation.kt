package com.assignment.driver.domain.models

data class TripLocation(
    val id: Long?,            // auto-generated in DB
    val tripId: String,
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val accuracy: Float?,
    val speed: Float?,
    val bearing: Float?
)
