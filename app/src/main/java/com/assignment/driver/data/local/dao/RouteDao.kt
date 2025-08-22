package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.RouteEntity
import kotlinx.coroutines.flow.Flow


/**
 * DAO for Route entities.
 *
 * Responsibilities:
 * - Bulk upsert routes fetched from a remote catalog.
 * - Provide reactive and direct reads of the full route list, ordered by name.
 * - Support clearing all routes (e.g., during reseed or logout if desired).
 *
 * Notes:
 * - upsertAll() replaces existing routes with the same primary key.
 * - observeAll() is the preferred source for UI (Flow emits on any change).
 */
@Dao
interface RouteDao {

    /**
     * Inserts or replaces the provided list of routes in a single transaction.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(routes: List<RouteEntity>)

    /**
     * Observes all routes, ordered alphabetically by name.
     * Emits whenever the routes table changes.
     */
    @Query("SELECT * FROM routes ORDER BY name ASC")
    fun observeAll(): Flow<List<RouteEntity>>

    /**
     * Returns all routes, ordered alphabetically by name.
     */
    @Query("SELECT * FROM routes ORDER BY name ASC")
    suspend fun getAll(): List<RouteEntity>

    /**
     * Deletes all routes.
     * Useful for reseeding or full cache resets.
     */
    @Query("DELETE FROM routes")
    suspend fun clear()
}

