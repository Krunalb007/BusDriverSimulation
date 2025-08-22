package com.assignment.driver.di

import com.assignment.driver.data.remote.CatalogRemoteDataSource
import com.assignment.driver.data.remote.FakeCatalogRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {
    @Binds @Singleton
    abstract fun bindCatalogRemote(impl: FakeCatalogRemoteDataSource): CatalogRemoteDataSource
}
