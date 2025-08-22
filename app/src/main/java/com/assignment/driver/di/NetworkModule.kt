package com.assignment.driver.di


import com.assignment.driver.data.remote.FakeTripsRemoteDataSource
import com.assignment.driver.data.remote.TripsRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds @Singleton
    abstract fun bindTripsRemote(ds: FakeTripsRemoteDataSource): TripsRemoteDataSource
}
