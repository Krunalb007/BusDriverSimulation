package com.assignment.driver.domain.models

data class Driver(
    val id: String,
    val name: String,
    val lastSyncedAt: Long?
)

