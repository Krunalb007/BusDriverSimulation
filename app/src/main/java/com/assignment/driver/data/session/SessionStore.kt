package com.assignment.driver.data.session

import com.assignment.driver.data.local.dao.SessionDao
import com.assignment.driver.data.local.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionStore
 *
 * Purpose:
 * - Provides a simple, centralized API to persist and observe the current session state,
 *   specifically the active driverId, using Room as durable storage.
 * - Enables offline session restore on app start without relying on encrypted/shared prefs.
 *
 * Responsibilities:
 * - Persist the active driverId (or clear it on logout) into the SessionEntity single-row table.
 * - Read the current driverId for synchronous flows (restore at startup).
 * - Expose a reactive Flow<String?> so UI or domain can react to login/logout changes.
 *
 * Threading:
 * - All DAO calls are suspend and should be made from a background dispatcher (e.g., IO).
 * - The Flow returned by observeDriverId() emits on database changes to the session row.
 *
 * Typical usage:
 * - On login success: setDriverId(driver.id)
 * - On logout: setDriverId(null)
 * - On app start: getDriverId() to restore the session
 * - For reactive UI: observeDriverId()
 */
@Singleton
class SessionStore @Inject constructor(
    private val sessionDao: SessionDao
) {

    /**
     * Persist or clear the current driverId in the session row.
     *
     * @param id The active driver's ID; pass null to clear the session.
     */
    suspend fun setDriverId(id: String?) {
        sessionDao.upsert(SessionEntity(driverId = id))
    }

    /**
     * Read the currently persisted driverId, if any.
     *
     * @return The active driver's ID, or null if no session is stored.
     */
    suspend fun getDriverId(): String? {
        return sessionDao.get()?.driverId
    }
}
