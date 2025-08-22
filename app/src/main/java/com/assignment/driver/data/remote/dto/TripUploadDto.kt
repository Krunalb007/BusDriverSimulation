package com.assignment.driver.data.remote.dto

/**
 * Payload sent to the server for uploading a completed trip and its recorded points.
 *
 * Purpose:
 * - Communicates the trip header (ids, timing) and the sequence of location samples
 *   collected while tracking.
 * - Designed to be serialized (e.g., via Moshi) and posted to a backend endpoint.
 *
 * Fields:
 * - tripId: Unique identifier for the trip (UUID).
 * - driverId: Identifier of the driver who performed the trip.
 * - routeId: Identifier of the route assigned to the trip.
 * - startTime: Trip start timestamp in epoch millis.
 * - endTime: Trip end timestamp in epoch millis (non-null for completed trips).
 * - points: Ordered list of recorded location samples for the trip.
 *
 * Notes:
 * - Ensure endTime is set when marking a trip as COMPLETED before constructing this payload.
 * - For large trips, consider chunking, compression, or summaries as future enhancements.
 */
data class TripUploadDto(
    val tripId: String,
    val driverId: String,
    val routeId: String,
    val startTime: Long,
    val endTime: Long,
    val points: List<TripPointDto>
)

/**
 * Network transfer object for an individual location sample within a trip upload.
 *
 * Fields:
 * - timestamp: Epoch millis when the sample was recorded.
 * - lat, lng: Latitude and longitude in WGS84.
 * - accuracy: Optional horizontal accuracy in meters (if available from provider).
 * - speed: Optional ground speed in meters/second (if available).
 * - bearing: Optional bearing in degrees (0–359, if available).
 *
 * Ordering:
 * - Points should be ordered by timestamp ascending for clarity on the server side.
 *
 * Privacy & Size:
 * - Consider redaction, thinning, or bounding depending on privacy requirements and payload size.
 */
data class TripPointDto(
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val accuracy: Float?,
    val speed: Float?,
    val bearing: Float?
)
