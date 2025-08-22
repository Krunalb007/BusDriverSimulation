package com.assignment.driver.di

import com.assignment.driver.data.remote.FakeTripsRemoteDataSource
import com.assignment.driver.data.remote.TripsRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * NetworkModule
 *
 * Purpose:
 * - Binds network-facing interfaces to their implementations for trip uploads.
 *
 * Bindings:
 * - TripsRemoteDataSource -> FakeTripsRemoteDataSource
 *
 * Notes:
 * - The fake implementation simulates latency and optional failures, making it ideal
 *   for local development and testing. Swap to a real implementation for production.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindTripsRemote(ds: FakeTripsRemoteDataSource): TripsRemoteDataSource
}
