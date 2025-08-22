package com.assignment.driver.data.local.dao

import androidx.room.*
import com.assignment.driver.data.local.entities.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(trip: TripEntity)

    @Update
    suspend fun update(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE status = :status")
    suspend fun getByStatus(status: String): List<TripEntity>

    @Query("SELECT * FROM trips WHERE status = 'ACTIVE' LIMIT 1")
    fun observeActiveTrip(): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TripEntity?

    @Query(
        """
    UPDATE trips
    SET status = :newStatus,
        endTime = :endTime,
        updatedAt = :updatedAt,
        locationCount = :locationCount,
        firstPointAt = :firstPointAt,
        lastPointAt = :lastPointAt
    WHERE id = :id
"""
    )
    suspend fun markStatus(
        id: String,
        newStatus: String,
        endTime: Long,
        updatedAt: Long,
        locationCount: Int,
        firstPointAt: Long?,
        lastPointAt: Long?
    )

    @Query("UPDATE trips SET status = :newStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatusOnly(id: String, newStatus: String, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM trips WHERE status = :status")
    fun observeCountByStatus(status: String): Flow<Int>

    @Query("SELECT * FROM trips ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentTrips(limit: Int): List<TripEntity>

    @Query("SELECT COUNT(*) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLocationCount(tripId: String): Int

    @Query("SELECT MIN(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getFirstTimestamp(tripId: String): Long?

    @Query("SELECT MAX(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLastTimestamp(tripId: String): Long?


}
