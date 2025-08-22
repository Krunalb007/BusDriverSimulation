package com.assignment.driver.data.local


import com.assignment.driver.data.repository.DriverRepository
import com.assignment.driver.data.repository.RouteRepository
import com.assignment.driver.domain.models.Driver
import com.assignment.driver.domain.models.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedManager @Inject constructor(
    private val driverRepo: DriverRepository,
    private val routeRepo: RouteRepository
) {
    private val seeded = AtomicBoolean(false)

    fun seedIfNeeded() {
        if (!seeded.compareAndSet(false, true)) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existing = driverRepo.getCurrent()
                if (existing == null) {
                    val now = System.currentTimeMillis()
                    val driver = Driver(
                        id = "driver-001",
                        name = "Alex Driver",
                        lastSyncedAt = null
                    )
                    driverRepo.upsert(driver)

                    val routes = listOf(
                        Route("route-101", "City Center Loop", "Central Station", "Old Town", now),
                        Route("route-102", "Airport Express", "Central Station", "International Airport", now),
                        Route("route-103", "Tech Park Shuttle", "Central Station", "Tech Park", now)
                    )
                    routeRepo.replaceAll(routes)

                    Timber.i("Seeded driver and routes")
                } else {
                    Timber.i("Seed skipped: driver exists")
                }
            } catch (t: Throwable) {
                Timber.e(t, "Seeding failed")
            }
        }
    }
}
