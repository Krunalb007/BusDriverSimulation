package com.assignment.driver.data.repository.impl

import com.assignment.driver.data.local.dao.TripDao
import com.assignment.driver.data.local.dao.TripLocationDao
import com.assignment.driver.data.mappers.toDomain
import com.assignment.driver.data.mappers.toEntity
import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation
import com.assignment.driver.domain.models.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * TripRepositoryImpl
 *
 * Purpose:
 * - Implements TripRepository on top of Room DAOs (TripDao, TripLocationDao).
 * - Manages trip lifecycle: create active, complete with stats, update status, and read/observe.
 *
 * Key behaviors:
 * - completeTrip(tripId, endTime): computes stats (count/first/last) from trip_locations
 *   and persists them into the Trip row together with status=COMPLETED and endTime.
 * - updateTripStatus(tripId, status): updates only the status/updatedAt for non-completion
 *   transitions (e.g., SYNCED) to avoid wiping endTime/stats.
 * - clearLocations(tripId): delete raw points after successful sync, leaving stats in Trip.
 *
 * Observability:
 * - observeActiveTrip(): emits the current ACTIVE trip (or null).
 * - observeRecentTrips(limit): emits recent trips ordered by updatedAt/createdAt (per DAO).
 *
 * Notes:
 * - Mapping between entity and domain is centralized in mappers.
 * - For performance, prefer DAO COUNT/MIN/MAX over retrieving all points for stats.
 */
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val locDao: TripLocationDao,
) : TripRepository {

    override suspend fun createActiveTrip(trip: Trip) {
        tripDao.insert(trip.toEntity())
    }

    override suspend fun completeTrip(tripId: String, endTime: Long) {
        val now = System.currentTimeMillis()
        // Compute stats before any clearing happens
        val count = locDao.getCount(tripId)
        val first = locDao.getFirstTimestamp(tripId)
        val last = locDao.getLastTimestamp(tripId)

        // Persist completion state and stats in the Trip row
        tripDao.markStatus(
            id = tripId,
            newStatus = TripStatus.COMPLETED.name,
            endTime = endTime,
            updatedAt = now,
            locationCount = count,
            firstPointAt = first,
            lastPointAt = last
        )
    }

    override suspend fun updateTripStatus(tripId: String, status: TripStatus) {
        val now = System.currentTimeMillis()
        // Do not touch endTime or stats on non-completion transitions
        tripDao.updateStatusOnly(tripId, status.name, now)
    }

    override fun observeActiveTrip(): Flow<Trip?> =
        tripDao.observeActiveTrip().map { it?.toDomain() }

    override suspend fun getTripById(tripId: String): Trip? =
        tripDao.getById(tripId)?.toDomain()

    override suspend fun getTripsByStatus(status: TripStatus): List<Trip> =
        tripDao.getByStatus(status.name).map { it.toDomain() }

    override suspend fun addLocation(point: TripLocation) {
        locDao.insert(point.toEntity())
    }

    override suspend fun addLocations(points: List<TripLocation>) {
        locDao.insertAll(points.map { it.toEntity() })
    }

    override suspend fun getLocations(tripId: String): List<TripLocation> =
        locDao.getByTripId(tripId).map { it.toDomain() }

    override suspend fun clearLocations(tripId: String) {
        // Implement the DAO delete method (e.g., DELETE FROM trip_locations WHERE tripId = :tripId)
        // and call it here.
        locDao.deleteByTripId(tripId)
    }

    override fun observeRecentTrips(limit: Int): Flow<List<Trip>> =
        tripDao.observeRecentTrips(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getLocationCount(tripId: String): Int =
        // Prefer the COUNT query for performance
        locDao.getCount(tripId)

    override suspend fun getLocationTimeRange(tripId: String): Pair<Long?, Long?> {
        val first = locDao.getFirstTimestamp(tripId)
        val last = locDao.getLastTimestamp(tripId)
        return first to last
    }
}
