package com.assignment.driver.data.local.dao

import androidx.room.*
import com.assignment.driver.data.local.entities.TripEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Trip entities.
 *
 * Responsibilities:
 * - Create and update trip rows, including completion stats and status changes.
 * - Query trips by status and id.
 * - Expose reactive streams for the active trip and recent trips.
 * - Provide lightweight aggregate helpers (counts and time range) from locations.
 *
 * Data integrity:
 * - markStatus() should be used to complete a trip, setting endTime and persisted stats.
 * - updateStatusOnly() is used for non-completion transitions (e.g., to SYNCED) and
 *   must not modify endTime or stats.
 *
 * Performance:
 * - Observe methods (Flow) are preferred for UI to auto-refresh on DB changes.
 * - Aggregate helpers (COUNT/MIN/MAX) are indexed by tripId in the locations table.
 */

@Dao
interface TripDao {

    /**
     * Inserts a new trip. Fails if id already exists.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(trip: TripEntity)

    /**
     * Updates the full trip row. Use with care; prefer targeted updates below.
     */
    @Update
    suspend fun update(trip: TripEntity)

    /**
     * Returns trips filtered by status.
     */
    @Query("SELECT * FROM trips WHERE status = :status")
    suspend fun getByStatus(status: String): List<TripEntity>

    /**
     * Observes the single active trip (if any). Emits null when none is active.
     */
    @Query("SELECT * FROM trips WHERE status = 'ACTIVE' LIMIT 1")
    fun observeActiveTrip(): Flow<TripEntity?>

    /**
     * Returns a trip by its id, or null if not found.
     */
    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TripEntity?

    /**
     * Marks a trip with a new status while also setting endTime and completion stats.
     * Use this for COMPLETED transitions to persist the final snapshot:
     * - endTime
     * - locationCount
     * - firstPointAt
     * - lastPointAt
     *
     * Do not use for SYNC or non-completion updates (see updateStatusOnly()).
     */
    @Query("""
        UPDATE trips
        SET status = :newStatus,
            endTime = :endTime,
            updatedAt = :updatedAt,
            locationCount = :locationCount,
            firstPointAt = :firstPointAt,
            lastPointAt = :lastPointAt
        WHERE id = :id
    """)
    suspend fun markStatus(
        id: String,
        newStatus: String,
        endTime: Long,
        updatedAt: Long,
        locationCount: Int,
        firstPointAt: Long?,
        lastPointAt: Long?
    )

    /**
     * Updates only the status and updatedAt timestamp.
     * Use this for non-completion transitions (e.g., COMPLETED -> SYNCED) so that
     * endTime and stats remain intact.
     */
    @Query("UPDATE trips SET status = :newStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatusOnly(id: String, newStatus: String, updatedAt: Long)

    /**
     * Observes a count of trips by status (e.g., ACTIVE, COMPLETED, SYNCED).
     */
    @Query("SELECT COUNT(*) FROM trips WHERE status = :status")
    fun observeCountByStatus(status: String): Flow<Int>

    /**
     * Returns the N most recently created trips (one-shot).
     * Prefer observeRecentTrips() for reactive lists in UI.
     */
    @Query("SELECT * FROM trips ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentTrips(limit: Int): List<TripEntity>

    /**
     * Helper aggregates from trip_locations for a single trip.
     * Typically used to compute completion stats before clearing locations.
     */
    @Query("SELECT COUNT(*) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLocationCount(tripId: String): Int

    @Query("SELECT MIN(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getFirstTimestamp(tripId: String): Long?

    @Query("SELECT MAX(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLastTimestamp(tripId: String): Long?

    /**
     * Observes the N most recently created trips.
     * Use this to drive the Recent Trips UI so it auto-refreshes on status changes (e.g., SYNCED).
     */
    @Query("SELECT * FROM trips ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentTrips(limit: Int): Flow<List<TripEntity>>
}

