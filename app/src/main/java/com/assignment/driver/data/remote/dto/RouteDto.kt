package com.assignment.driver.data.remote.dto

/**
 * Network transfer object representing a Route as delivered by the remote API.
 *
 * Purpose:
 * - Encapsulates the minimal fields needed to refresh the local route catalog.
 * - Serves as the boundary type for Retrofit/Moshi (or any HTTP client) parsing.
 *
 * Fields:
 * - id: Stable, unique route identifier (used as primary key locally).
 * - name: Human-readable route name shown in the UI.
 * - startPoint: Optional starting stop/name for display.
 * - endPoint: Optional ending stop/name for display.
 * - updatedAt: Server-side last-updated timestamp (epoch millis). Useful for
 *              conflict resolution or incremental syncs if implemented.
 *
 * Mapping:
 * - Convert this DTO to a domain Route or a Room RouteEntity via a mapper.
 * - Keep the DTO decoupled from persistence and UI layers.
 */
data class RouteDto(
    val id: String,
    val name: String,
    val startPoint: String?,
    val endPoint: String?,
    val updatedAt: Long
)
