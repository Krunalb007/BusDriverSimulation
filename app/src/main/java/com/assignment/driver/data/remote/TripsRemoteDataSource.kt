package com.assignment.driver.data.remote

import com.assignment.driver.data.remote.dto.TripUploadDto

interface TripsRemoteDataSource {
    suspend fun uploadTrip(payload: TripUploadDto): Result<Unit>
}
