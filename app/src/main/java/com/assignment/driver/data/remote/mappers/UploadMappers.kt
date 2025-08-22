package com.assignment.driver.data.remote.mappers

import com.assignment.driver.data.remote.dto.TripPointDto
import com.assignment.driver.data.remote.dto.TripUploadDto
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation

/**
 * Builders for trip upload payloads.
 *
 * Purpose:
 * - Transform a completed Trip and its recorded TripLocation samples into a compact
 *   payload (TripUploadDto) for submission to the backend.
 *
 * Notes:
 * - endTime must be set for completed trips. If the domain model endTime is null,
 *   this function falls back to System.currentTimeMillis() to avoid sending invalid data.
 * - The points list should be ordered by timestamp ascending for clarity server-side.
 */
fun toUploadDto(
    trip: Trip,
    locations: List<TripLocation>
): TripUploadDto {
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
