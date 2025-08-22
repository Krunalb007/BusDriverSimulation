package com.assignment.driver.di

import com.assignment.driver.data.remote.CatalogRemoteDataSource
import com.assignment.driver.data.remote.FakeCatalogRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * CatalogModule
 *
 * Purpose:
 * - Binds the catalog remote data source interface to its implementation.
 *
 * Bindings:
 * - CatalogRemoteDataSource -> FakeCatalogRemoteDataSource
 *
 * Notes:
 * - Suitable for debug/offline development. For production, bind a real Retrofit-based
 *   implementation here (and optionally swap via build flavors).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    /**
     * Bind the fake catalog remote source as the default implementation.
     */
    @Binds
    @Singleton
    abstract fun bindCatalogRemote(impl: FakeCatalogRemoteDataSource): CatalogRemoteDataSource
}
