package com.assignment.driver.di

import android.content.Context
import androidx.room.Room
import com.assignment.driver.data.local.AppDatabase
import com.assignment.driver.data.local.dao.DriverDao
import com.assignment.driver.data.local.dao.RouteDao
import com.assignment.driver.data.local.dao.SessionDao
import com.assignment.driver.data.local.dao.TripDao
import com.assignment.driver.data.local.dao.TripLocationDao
import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.data.repository.impl.DriverRepositoryImpl
import com.assignment.driver.data.repository.impl.RouteRepositoryImpl
import com.assignment.driver.data.repository.impl.TripRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DatabaseModule
 *
 * Purpose:
 * - Creates and exposes the Room database and DAOs as singleton dependencies.
 *
 * Configuration:
 * - Database name: "busdriver.db"
 * - fallbackToDestructiveMigration(true): acceptable for demo/assignment; in a
 *   production app, proper migrations should be provided instead.
 *
 * Exposed DAOs:
 * - DriverDao, RouteDao, TripDao, TripLocationDao, SessionDao
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provide the Room database singleton.
     */
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "busdriver.db")
            .fallbackToDestructiveMigration(true)
            .build()

    /** Provide DAO singletons from the Room database. */
    @Provides fun provideDriverDao(db: AppDatabase): DriverDao = db.driverDao()
    @Provides fun provideRouteDao(db: AppDatabase): RouteDao = db.routeDao()
    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideTripLocationDao(db: AppDatabase): TripLocationDao = db.tripLocationDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}

/**
 * RepositoryModule
 *
 * Purpose:
 * - Binds repository interfaces to their concrete Room-backed implementations.
 *
 * Bindings:
 * - DriverRepository -> DriverRepositoryImpl
 * - RouteRepository  -> RouteRepositoryImpl
 * - TripRepository   -> TripRepositoryImpl
 *
 * Notes:
 * - Keeps UI/domain layers decoupled from persistence details via interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindDriverRepository(impl: DriverRepositoryImpl): DriverRepository

    @Binds @Singleton
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Binds @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository
}
