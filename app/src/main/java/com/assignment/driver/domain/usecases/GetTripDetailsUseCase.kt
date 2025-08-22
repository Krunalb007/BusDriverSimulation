package com.assignment.driver.domain.usecases

import com.assignment.driver.data.repository.TripRepository

class GetTripDetailsUseCase @javax.inject.Inject constructor(
    private val tripRepo: TripRepository
) {
    data class Details(
        val trip: com.assignment.driver.domain.models.Trip,
        val locationCount: Int,
        val firstTs: Long?,
        val lastTs: Long?
    )
    suspend fun get(tripId: String): Details? {
        val trip = tripRepo.getTripById(tripId) ?: return null
        val count = tripRepo.getLocationCount(tripId)
        val (first, last) = tripRepo.getLocationTimeRange(tripId)
        return Details(trip, count, first, last)
    }
    suspend fun listRecent(limit: Int = 20): List<com.assignment.driver.domain.models.Trip> =
        tripRepo.getRecentTrips(limit)
}
