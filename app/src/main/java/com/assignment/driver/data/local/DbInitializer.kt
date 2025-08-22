package com.assignment.driver.data.local

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbInitializer @Inject constructor(
    private val app: Application,
    private val db: AppDatabase
)
