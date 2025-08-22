package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation
import com.assignment.driver.domain.models.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun createActiveTrip(trip: Trip)
    suspend fun completeTrip(tripId: String, endTime: Long)
    suspend fun updateTripStatus(tripId: String, status: TripStatus)
    fun observeActiveTrip(): Flow<Trip?>
    suspend fun getTripById(tripId: String): Trip?
    suspend fun getTripsByStatus(status: TripStatus): List<Trip>
    suspend fun addLocation(point: TripLocation)
    suspend fun addLocations(points: List<TripLocation>)
    suspend fun getLocations(tripId: String): List<TripLocation>
    suspend fun clearLocations(tripId: String)
    suspend fun getRecentTrips(limit: Int = 20): List<Trip>
    suspend fun getLocationCount(tripId: String): Int
    suspend fun getLocationTimeRange(tripId: String): Pair<Long?, Long?>

}
