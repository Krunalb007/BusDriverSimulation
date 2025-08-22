package com.assignment.driver.data.repository

import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation
import com.assignment.driver.domain.models.TripStatus
import kotlinx.coroutines.flow.Flow

/**
 * TripRepository
 *
 * Contract for managing trips and their recorded locations.
 *
 * Lifecycle operations:
 * - createActiveTrip(trip): create a new ACTIVE trip.
 * - completeTrip(tripId, endTime): mark trip COMPLETED and persist stats (count/first/last).
 * - updateTripStatus(tripId, status): update status only (e.g., to SYNCED) without altering endTime/stats.
 *
 * Locations:
 * - addLocation / addLocations: append recorded points for an active trip.
 * - getLocations(tripId): retrieve points (before they are cleared post-sync).
 * - clearLocations(tripId): remove raw points after successful sync (stats stay on Trip).
 *
 * Queries & observation:
 * - observeActiveTrip(): reactive stream of current ACTIVE trip (or null).
 * - getTripById(tripId): single trip by id.
 * - getTripsByStatus(status): trips filtered by status.
 * - observeRecentTrips(limit): reactive list of recent trips (e.g., ordered by updatedAt).
 * - getLocationCount(tripId): fast count via DAO COUNT.
 * - getLocationTimeRange(tripId): min/max timestamps for points (for details).
 */
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

    fun observeRecentTrips(limit: Int = 20): Flow<List<Trip>>
    suspend fun getLocationCount(tripId: String): Int
    suspend fun getLocationTimeRange(tripId: String): Pair<Long?, Long?>
}
