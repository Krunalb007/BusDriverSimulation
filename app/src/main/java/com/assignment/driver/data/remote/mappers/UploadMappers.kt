package com.assignment.driver.data.remote.mappers

import com.assignment.driver.data.remote.dto.TripPointDto
import com.assignment.driver.data.remote.dto.TripUploadDto
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation

fun toUploadDto(trip: Trip, locations: List<TripLocation>): TripUploadDto {
    return TripUploadDto(
        tripId = trip.id,
        driverId = trip.driverId,
        routeId = trip.routeId,
        startTime = trip.startTime,
        endTime = trip.endTime ?: System.currentTimeMillis(),
        points = locations.map {
            TripPointDto(
                timestamp = it.timestamp,
                lat = it.lat,
                lng = it.lng,
                accuracy = it.accuracy,
                speed = it.speed,
                bearing = it.bearing
            )
        }
    )
}
