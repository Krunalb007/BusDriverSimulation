package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 Purpose:
 - Represents a driver profile stored locally in the Room database.
 - Used as the single source of truth for the current driver's identity and metadata.

 Table: drivers
 - id: Unique identifier for the driver (e.g., "driver-001").
 - name: Human-readable name of the driver.
 - lastSyncedAt: Timestamp (epoch millis) recording the last time this driver record
   was updated from a remote source (nullable if never synced).

 Typical usage:
 - Seed a default driver on first app launch.
 - Read to display "Logged in as {name} ({id})".
 - Update lastSyncedAt after a catalog refresh completes.

 Notes:
 - Keep this table small; one or few rows are expected.
 - Authentication tokens are NOT stored here; session is handled separately in SessionEntity.*/

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lastSyncedAt: Long?
)

