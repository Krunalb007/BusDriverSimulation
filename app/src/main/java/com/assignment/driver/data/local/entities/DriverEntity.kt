package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lastSyncedAt: Long?
)
