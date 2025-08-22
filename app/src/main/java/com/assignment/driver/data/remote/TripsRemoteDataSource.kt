package com.assignment.driver.data.remote

import com.assignment.driver.data.remote.dto.TripUploadDto

/**
 * Abstraction for uploading trips to a remote backend.
 *
 * Method:
 * - uploadTrip(payload): Transmits a completed trip with its points to the server.
 *
 * Return:
 * - Uses Result<Unit> to communicate success or failure (exceptions are wrapped).
 *
 * Implementation notes:
 * - A real implementation would handle auth, timeouts, retries, and error mapping.
 * - Consider compression or chunking for large point sets in production.
 */
interface TripsRemoteDataSource {
    suspend fun uploadTrip(payload: TripUploadDto): Result<Unit>
}
