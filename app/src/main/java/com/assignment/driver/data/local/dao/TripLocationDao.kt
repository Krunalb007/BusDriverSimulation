package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.TripLocationEntity

@Dao
interface TripLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(points: List<TripLocationEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(point: TripLocationEntity)

    @Query("SELECT * FROM trip_locations WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getByTripId(tripId: String): List<TripLocationEntity>

    @Query("DELETE FROM trip_locations WHERE tripId = :tripId")
    suspend fun deleteByTripId(tripId: String)

    @Query("SELECT COUNT(*) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getCount(tripId: String): Int

    @Query("SELECT MIN(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getFirstTimestamp(tripId: String): Long?

    @Query("SELECT MAX(timestamp) FROM trip_locations WHERE tripId = :tripId")
    suspend fun getLastTimestamp(tripId: String): Long?
}
