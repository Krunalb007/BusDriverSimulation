package com.assignment.driver.domain.models

enum class TripStatus { ACTIVE, COMPLETED, SYNCED }

data class Trip(
    val id: String,           // UUID
    val driverId: String,
    val routeId: String,
    val startTime: Long,
    val endTime: Long?,
    val status: TripStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val locationCount: Int = 0,
    val firstPointAt: Long? = null,
    val lastPointAt: Long? = null
)
