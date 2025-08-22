package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.RouteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(routes: List<RouteEntity>)

    @Query("SELECT * FROM routes ORDER BY name ASC")
    fun observeAll(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes ORDER BY name ASC")
    suspend fun getAll(): List<RouteEntity>

    @Query("DELETE FROM routes")
    suspend fun clear()
}
