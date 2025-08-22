package com.assignment.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.driver.data.local.entities.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(driver: DriverEntity)

    @Query("SELECT * FROM drivers LIMIT 1")
    fun observeCurrentDriver(): Flow<DriverEntity?>

    @Query("SELECT * FROM drivers LIMIT 1")
    suspend fun getCurrentDriver(): DriverEntity?
}
