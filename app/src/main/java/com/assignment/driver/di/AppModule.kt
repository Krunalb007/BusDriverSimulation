package com.assignment.driver.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule
 *
 * Purpose:
 * - Provides application-scoped Android dependencies that are needed across the app.
 *
 * Provided bindings:
 * - Context: the application context, annotated with @ApplicationContext for clarity.
 *
 * Notes:
 * - Add more @Provides methods here for app-wide singletons that do not belong to
 *   a more specific module (e.g., logger config, time providers).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Application-level Context.
     *
     * Exposes the @ApplicationContext so other modules/classes can depend on it
     * without holding Activity references.
     */
    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext ctx: Context): Context = ctx
}
