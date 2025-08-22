package com.assignment.driver.domain.usecases

import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripStatus
import com.assignment.driver.domain.session.SessionManager
import java.util.UUID
import javax.inject.Inject

class StartTripUseCase @Inject constructor(
    private val tripRepo: TripRepository,
    private val session: SessionManager
) {
    suspend fun start(routeId: String, now: Long = System.currentTimeMillis()): Trip {
        val driver = session.currentDriver.value
            ?: throw IllegalStateException("Must be logged in to start a trip")
        val trip = Trip(
            id = UUID.randomUUID().toString(),
            driverId = driver.id,
            routeId = routeId,
            startTime = now,
            endTime = null,
            status = TripStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )
        tripRepo.createActiveTrip(trip)
        return trip
    }
}

