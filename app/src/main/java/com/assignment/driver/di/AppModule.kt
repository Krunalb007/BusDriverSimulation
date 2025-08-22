package com.assignment.driver.di


import android.content.Context
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Placeholders for future provides (DB, Retrofit, etc.)
    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext ctx: Context): Context = ctx
}
