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


fun DriverEntity.toDomain() = Driver(id, name, lastSyncedAt)
fun Driver.toEntity() = DriverEntity(id, name, lastSyncedAt)

fun RouteEntity.toDomain() = Route(id, name, startPoint, endPoint, lastUpdatedAt)
fun Route.toEntity() = RouteEntity(id, name, startPoint, endPoint, lastUpdatedAt)

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

fun Trip.toEntity() = TripEntity(
    id = id,
    driverId = driverId,
    routeId = routeId,
    startTime = startTime,
    endTime = endTime,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TripLocationEntity.toDomain() = TripLocation(
    id = id,
    tripId = tripId,
    timestamp = timestamp,
    lat = lat,
    lng = lng,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing
)

fun TripLocation.toEntity() = TripLocationEntity(
    id = id ?: 0,
    tripId = tripId,
    timestamp = timestamp,
    lat = lat,
    lng = lng,
    accuracy = accuracy,
    speed = speed,
    bearing = bearing
)
