package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

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
