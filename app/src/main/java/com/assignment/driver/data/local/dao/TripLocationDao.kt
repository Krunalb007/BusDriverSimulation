package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.TripLocationEntity

/**
 * DAO for TripLocation entities (location points recorded during a trip).
 *
 * Responsibilities:
 * - Insert single or multiple points (ignoring duplicates by primary key).
 * - Read ordered points for a given trip (ascending by timestamp).
 * - Delete all points for a trip (e.g., after a successful sync).
 * - Provide simple aggregates (count, first/last timestamp) used to persist stats
 *   onto the Trip row at completion time.
 *
 * Data lifecycle:
 * - Points are appended while a trip is ACTIVE.
 * - On trip completion, aggregate stats are computed from this table and written
 *   into the corresponding Trip row.
 * - After a successful server upload, rows for that trip can be cleared to save space,
 *   while the Trip’s persisted stats remain available.
 */
@Dao
interface TripLocationDao {

    /**
     * Bulk insert of points; duplicates are ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(points: List<TripLocationEntity>)

    /**
     * Insert a single point; duplicates are ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(point: TripLocationEntity)

    /**
     * Returns all points for a trip, ordered by timestamp ascending.
     * Useful for upload payload construction and debugging.
     */
    @Query("SELECT * FROM trip_locations WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getByTripId(tripId: String): List<TripLocationEntity>

    /**
     * Deletes all points for the given trip.
     * Typically called after a successful sync to reclaim storage.
     */
    @Query("DELETE FROM trip_locations WHERE tripId = :tripId")
    suspend fun deleteByTripId(tripId: String)

    /**
     * Aggregates for a trip’s points, used to persist final stats onto the Trip row.
     */
    @Query("SELECT COUNT(*) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getCount(tripId: String): Int

    @Query("SELECT MIN(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getFirstTimestamp(tripId: String): Long?

    @Query("SELECT MAX(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLastTimestamp(tripId: String): Long?
}

