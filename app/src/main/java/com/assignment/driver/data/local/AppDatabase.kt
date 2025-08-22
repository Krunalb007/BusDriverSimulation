package com.assignment.driver.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assignment.driver.data.local.dao.DriverDao
import com.assignment.driver.data.local.dao.RouteDao
import com.assignment.driver.data.local.dao.SessionDao
import com.assignment.driver.data.local.dao.TripDao
import com.assignment.driver.data.local.dao.TripLocationDao
import com.assignment.driver.data.local.entities.DriverEntity
import com.assignment.driver.data.local.entities.RouteEntity
import com.assignment.driver.data.local.entities.SessionEntity
import com.assignment.driver.data.local.entities.TripEntity
import com.assignment.driver.data.local.entities.TripLocationEntity

/**
 * Central Room database for the Bus Driver app.
 *
 * Responsibilities:
 * - Declares all persisted entities used by the offline-first data layer.
 * - Exposes strongly-typed DAO accessors for CRUD and query operations.
 * - Provides a single source of truth for Driver, Route, Trip, TripLocation and Session data.
 *
 * Schema overview:
 * - DriverEntity: current driver profile persisted locally for offline session restore.
 * - RouteEntity: route catalog shown to the user and refreshed when online.
 * - TripEntity: trip headers (id, route/driver references, start/end times, status, stats).
 * - TripLocationEntity: raw location points captured during an active trip.
 * - SessionEntity: a tiny key-value row holding active session (e.g., current driverId).
 *
 * Versioning:
 * - version = 1 (fresh schema). If you change entities, bump the version and add migrations.
 *
 * exportSchema:
 * - Disabled for this project (exportSchema = false). Enable + provide a schema folder
 *   for long-lived apps to track migrations.
 *
 * Threading:
 * - Room ensures DAO methods annotated as suspend are safe to call from Dispatchers.IO.
 * - Always avoid long-running work on the main thread.
 */
@Database(
    entities = [
        DriverEntity::class,
        RouteEntity::class,
        TripEntity::class,
        TripLocationEntity::class,
        SessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Data access for DriverEntity (create/update current driver, fetch current).
     */
    abstract fun driverDao(): DriverDao

    /**
     * Data access for RouteEntity (replace catalog, query for display).
     */
    abstract fun routeDao(): RouteDao

    /**
     * Data access for TripEntity (create trips, update status/times/stats, list recents).
     */
    abstract fun tripDao(): TripDao

    /**
     * Data access for TripLocationEntity (insert/read points for active/completed trips).
     */
    abstract fun tripLocationDao(): TripLocationDao

    /**
     * Data access for SessionEntity (persist/restore current driverId for offline login).
     */
    abstract fun sessionDao(): SessionDao
}
