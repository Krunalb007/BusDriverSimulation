package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.DriverEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing the current Driver.
 *
 * Responsibilities:
 * - Upsert (insert or replace) the single driver record used by the app.
 * - Provide both reactive (Flow) and direct access to the current driver row.
 *
 * Contract:
 * - The app persists at most one driver locally; queries use LIMIT 1.
 * - observeCurrentDriver() emits whenever the driver row changes, enabling
 *   UI to react to catalog refreshes or local edits.
 *
 * Threading:
 * - All suspend functions must be called from a coroutine.
 * - Room handles query threading internally; callers should dispatch on IO as needed.
 */
@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(driver: DriverEntity)

    /**
     * Observes the current (single) driver stored locally.
     * Emits null if the table is empty.
     */
    @Query("SELECT * FROM drivers LIMIT 1")
    fun observeCurrentDriver(): Flow<DriverEntity?>

    /**
     * Returns the current (single) driver stored locally, or null if none exists.
     */
    @Query("SELECT * FROM drivers LIMIT 1")
    suspend fun getCurrentDriver(): DriverEntity?
}

