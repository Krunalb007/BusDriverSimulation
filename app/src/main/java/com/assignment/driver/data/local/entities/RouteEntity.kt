package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

/**
Purpose:
- Represents a transit route available to the driver.
- Serves as local cache for offline access, refreshed from a remote source when online.

Table: routes
- id: Stable unique ID for the route (e.g., "route-101").
- name: User-facing route name (indexed for quick search/filter).
- startPoint/endPoint: Optional endpoints for display purposes.
- lastUpdatedAt: Timestamp (epoch millis) of the last time this route was updated.

Indexes:
- name: Improves LIKE/filter queries or simple searches by route name.

Typical usage:
- Populate a list of routes for the driver to start trips against.
- Refresh periodically (WorkManager) and replace/merge records in Room.

Notes:
- Keep routes lightweight; heavy details can be stored in a separate table if needed.*/
@Entity(
    tableName = "routes",
    indices = [Index(value = ["name"])]
)
data class RouteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startPoint: String?,
    val endPoint: String?,
    val lastUpdatedAt: Long?
)
