package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

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
