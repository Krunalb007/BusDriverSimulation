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

/**
 * One-time seed initializer for local data on first app launch.
 *
 * Purpose:
 * - Ensures the app is usable offline immediately after installation by inserting:
 *   - A default driver (id: "driver-001", name: "Alex Driver")
 *   - A small set of demo routes (route-101/102/103)
 *
 * Behavior:
 * - seedIfNeeded() is idempotent at process level: guarded by an AtomicBoolean to avoid
 *   duplicate seeding in the same process.
 * - It also checks the DriverRepository for an existing current driver; if present, seeding
 *   is skipped. This allows the method to be safely called from Application.onCreate().
 *
 * Threading:
 * - Seeding work is dispatched on Dispatchers.IO to avoid blocking the main thread.
 *
 * Error handling:
 * - Exceptions during seeding are caught and logged via Timber; the app continues to run.
 *
 * Usage:
 * - Inject into the Application class and call seedIfNeeded() in onCreate().
 * - If you later introduce remote bootstrap, the initial seed can be reduced or removed.
 */
@Singleton
class SeedManager @Inject constructor(
    private val driverRepo: DriverRepository,
    private val routeRepo: RouteRepository
) {

    // Prevents duplicate seeding within the same process lifetime.
    private val seeded = AtomicBoolean(false)

    /**
     * Seeds a default driver and a small set of routes, only if not already present.
     *
     * Logic:
     * - Returns immediately if another call in this process already seeded (AtomicBoolean).
     * - Checks for an existing current driver; if found, skips seeding.
     * - Otherwise, inserts:
     *   - Driver(id="driver-001", name="Alex Driver")
     *   - Three demo routes with current timestamp
     */
    fun seedIfNeeded() {
        if (!seeded.compareAndSet(false, true)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existing = driverRepo.getCurrent()
                if (existing == null) {
                    val now = System.currentTimeMillis()

                    // Seed a default driver usable for offline login/demo
                    val driver = Driver(
                        id = "driver-001",
                        name = "Alex Driver",
                        lastSyncedAt = null
                    )
                    driverRepo.upsert(driver)

                    // Seed a small catalog of routes
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