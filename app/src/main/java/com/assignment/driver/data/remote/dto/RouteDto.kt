package com.assignment.driver.data.remote.dto

data class RouteDto(
    val id: String,
    val name: String,
    val startPoint: String?,
    val endPoint: String?,
    val updatedAt: Long
)