package com.assignment.driver.data.remote.mappers

import com.assignment.driver.data.remote.dto.RouteDto
import com.assignment.driver.domain.models.Route

/**
 * Remote-to-domain mappers for catalog data.
 *
 * Purpose:
 * - Convert network DTOs (parsed from API responses) into domain models
 *   used by the application and persistence layers.
 *
 * Guidelines:
 * - Keep DTOs isolated to the remote layer; map them at the boundary.
 * - Avoid putting API-specific fields into domain models.
 */
fun RouteDto.toDomain(): Route = Route(
    id = id,
    name = name,
    startPoint = startPoint,
    endPoint = endPoint,
    lastUpdatedAt = updatedAt
)
