package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
Purpose:
- Represents a driver's trip lifecycle and summary, persisted locally.
- Serves as a durable record even after raw location points are cleaned up.

Table: trips
- id: Unique trip identifier (UUID).
- driverId: The driver who performed the trip (indexed for driver-based queries).
- routeId: The route chosen for the trip (indexed).
- startTime: Trip start timestamp (epoch millis).
- endTime: Trip end timestamp (nullable until completed).
- status: Trip lifecycle state: "ACTIVE", "COMPLETED", "SYNCED" (indexed).
- createdAt: Time the trip record was created.
- updatedAt: Time the trip record was last updated (e.g., status change to SYNCED).
- locationCount: Persisted count of recorded points (computed at completion).
- firstPointAt: Timestamp of the earliest recorded point for the trip.
- lastPointAt: Timestamp of the latest recorded point for the trip.

Indexes:
- driverId, routeId, status: For efficient filtering and lists.

Typical lifecycle:
1) ACTIVE: created at start; raw points stored in trip_locations.
2) COMPLETED: endTime set; stats (count/first/last) computed and stored here.
3) SYNCED: after successful upload, status flips to SYNCED; raw points may be cleared.

Notes:
- Storing stats here allows showing details even after trip_locations are cleared.
- updatedAt can be used to order "Recent Trips" so status transitions bubble to top.*/
@Entity(
    tableName = "trips",
    indices = [Index(value = ["driverId"]), Index(value = ["routeId"]), Index(value = ["status"])]
)
data class TripEntity(
    @PrimaryKey val id: String,   // UUID
    val driverId: String,
    val routeId: String,
    val startTime: Long,
    val endTime: Long?,
    val status: String,           // ACTIVE, COMPLETED, SYNCED
    val createdAt: Long,
    val updatedAt: Long,
    val locationCount: Int = 0,
    val firstPointAt: Long? = null,
    val lastPointAt: Long? = null
)
