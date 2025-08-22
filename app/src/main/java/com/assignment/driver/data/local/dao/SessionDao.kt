package com.assignment.driver.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for persisting lightweight session state.
 *
 * Responsibilities:
 * - Store and retrieve a single session row keyed by 'current'.
 * - Provide both reactive observation and direct reads.
 *
 * Usage:
 * - Persist the currently logged-in driverId to enable offline restoration.
 * - Observe session to toggle UI/auth state reactively.
 */
@Dao
interface SessionDao {

    /**
     * Inserts or replaces the current session row.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity)

    /**
     * Returns the current session row (key = 'current'), or null if not set.
     */
    @Query("SELECT * FROM session WHERE sessionKey = 'current' LIMIT 1")
    suspend fun get(): SessionEntity?

    /**
     * Observes the current session row (key = 'current').
     * Emits null if the session is cleared.
     */
    @Query("SELECT * FROM session WHERE sessionKey = 'current' LIMIT 1")
    fun observe(): Flow<SessionEntity?>
}
