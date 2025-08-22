package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val sessionKey: String = "current",
    val driverId: String?
)
