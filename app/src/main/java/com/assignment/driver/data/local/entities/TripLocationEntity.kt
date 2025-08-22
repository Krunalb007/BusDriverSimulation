package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

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
