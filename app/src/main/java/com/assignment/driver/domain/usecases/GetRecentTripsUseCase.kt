package com.assignment.driver.domain.usecases

import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentTripsUseCase @Inject constructor(
    private val tripRepo: TripRepository
) {
    suspend fun get(limit: Int = 20): Flow<List<Trip>> = tripRepo.observeRecentTrips(limit)
}
