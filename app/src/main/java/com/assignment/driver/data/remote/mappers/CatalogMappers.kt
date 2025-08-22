package com.assignment.driver.data.remote.mappers

import com.assignment.driver.data.remote.dto.RouteDto
import com.assignment.driver.domain.models.Route

fun RouteDto.toDomain() = Route(
    id = id,
    name = name,
    startPoint = startPoint,
    endPoint = endPoint,
    lastUpdatedAt = updatedAt
)