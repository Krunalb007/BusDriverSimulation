package com.assignment.driver.data.repository.impl

import android.util.Log
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
        // implement in TripLocationDao (e.g., DELETE FROM trip_locations WHERE tripId = :tripId)
        // and call it here
    }

    override fun observeRecentTrips(limit: Int): Flow<List<Trip>> =
        tripDao.observeRecentTrips(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getLocationCount(tripId: String): Int =
        locDao.getByTripId(tripId).size // or prefer DAO COUNT query if added

    // If you added DAO COUNT/MIN/MAX methods, use them directly:
    override suspend fun getLocationTimeRange(tripId: String): Pair<Long?, Long?> {
        val points = locDao.getByTripId(tripId)
        val first = points.minOfOrNull { it.timestamp }
        val last = points.maxOfOrNull { it.timestamp }
        return first to last

    }

}
