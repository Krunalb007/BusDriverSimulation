package com.assignment.driver.data.mappers

import com.assignment.driver.data.local.entities.DriverEntity
import com.assignment.driver.data.local.entities.RouteEntity
import com.assignment.driver.data.local.entities.TripEntity
import com.assignment.driver.data.local.entities.TripLocationEntity
import com.assignment.driver.domain.models.Driver
import com.assignment.driver.domain.models.Route
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripLocation
import com.assignment.driver.domain.models.TripStatus

/**
 * Mapping extensions between Room entities and domain models.
 *
 * Goals:
 * - Keep persistence-layer types (Room entities) decoupled from domain-layer models.
 * - Provide a single source of truth for converting in both directions.
 * - Make repository implementations concise and consistent.
 *
 * Notes:
 * - TripEntity <-> Trip includes persisted stats (locationCount, firstPointAt, lastPointAt).
 * - Ensure any schema/domain changes are reflected here to avoid stale mappings.
 */

/* ===== Driver ===== */

fun DriverEntity.toDomain(): Driver = Driver(
    id = id,
    name = name,
    lastSyncedAt = lastSyncedAt
)

fun Driver.toEntity(): DriverEntity = DriverEntity(
    id = id,
    name = name,
    lastSyncedAt = lastSyncedAt
)

/* ===== Route ===== */

fun RouteEntity.toDomain(): Route = Route(
    id = id,
    name = name,
    startPoint = startPoint,
    endPoint = endPoint,
    lastUpdatedAt = lastUpdatedAt
)

fun Route.toEntity(): RouteEntity = RouteEntity(
    id = id,
    name = name,
    startPoint = startPoint,
    endPoint = endPoint,
    lastUpdatedAt = lastUpdatedAt
)

/* ===== Trip ===== */

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    driverId = driverId,
    routeId = routeId,
    startTime = startTime,
    endTime = endTime,
    status = TripStatus.valueOf(status),
    createdAt = createdAt,
    updatedAt = updatedAt,
    locationCount = locationCount,
    firstPointAt = firstPointAt,
    lastPointAt = lastPointAt
)

fun Trip.toEntity(): TripEntity = TripEntity(
    id = id,
    driverId = driverId,
    routeId = routeId,
    startTime = startTime,
    endTime = endTime,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    locationCount = locationCount,
    firstPointAt = firstPointAt,
    lastPointAt = lastPointAt
)

/* ===== TripLocation ===== */

fun TripLocationEntity.toDomain(): TripLocation = TripLocation(
    id = id,
    tripId = tripId,
    timestamp = timestamp,
    lat = lat,
    lng = lng,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing
)

fun TripLocation.toEntity(): TripLocationEntity = TripLocationEntity(
    id = id ?: 0L,
    tripId = tripId,
    timestamp = timestamp,
    lat = lat,
    lng = lng,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing
)
