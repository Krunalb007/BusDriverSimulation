package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/** Purpose:
- Stores individual GPS samples (breadcrumbs) recorded during an ACTIVE trip.
- Acts as the raw dataset used to compute trip summaries and upload payloads.

Table: trip_locations
- id: Auto-generated primary key.
- tripId: Foreign key reference to trips.id (indexed for per-trip queries).
- timestamp: Epoch millis when the sample was recorded (indexed for time-range queries).
- lat/lng: Latitude and longitude in decimal degrees.
- accuracy: Estimated horizontal accuracy in meters (nullable if not provided).
- speed: Speed in meters/second (nullable).
- bearing: Bearing/heading in degrees (nullable).

Typical usage:
- Insert periodically while the foreground service runs.
- On trip completion, compute stats (count/min/max) from this table and persist into trips.
- After successful sync, you may clear rows for that trip to save space.

Notes:
- Keep rows ordered by timestamp for efficient processing.
- Consider thinning/sampling if you need to control DB growth.*/
@Entity(
    tableName = "trip_locations",
    indices = [Index(value = ["tripId"]), Index(value = ["timestamp"])]
)
data class TripLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: String,
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val accuracy: Float?,
    val speed: Float?,
    val bearing: Float?
)
