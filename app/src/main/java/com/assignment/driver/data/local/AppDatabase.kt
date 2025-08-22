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
    abstract fun driverDao(): DriverDao
    abstract fun routeDao(): RouteDao
    abstract fun tripDao(): TripDao
    abstract fun tripLocationDao(): TripLocationDao
    abstract fun sessionDao(): SessionDao
}

