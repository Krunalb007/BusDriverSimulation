package com.assignment.driver.domain.usecases

import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActiveTripUseCase @Inject constructor(
    private val tripRepo: TripRepository
) {
    fun observe(): Flow<Trip?> = tripRepo.observeActiveTrip()
}
