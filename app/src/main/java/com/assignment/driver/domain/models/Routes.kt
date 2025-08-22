package com.assignment.driver.domain.models

data class Route(
    val id: String,
    val name: String,
    val startPoint: String?,
    val endPoint: String?,
    val lastUpdatedAt: Long?
)
