package com.assignment.driver.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Purpose:
- Stores the currently "logged-in" driver session for offline persistence.
- Allows restoring session on app restart without network.

Table: session
- sessionKey: Always "current" (single-row table design).
- driverId: The id of the logged-in driver, or null if logged out.

Typical usage:
- On login: set driverId = selected/current driver id.
- On logout: set driverId = null.
- On app start: read this row to restore session state in memory.

Notes:
- Single-row table pattern avoids shared preferences and fits Room-only persistence.
- If you need multi-user support, switch to storing multiple sessions or last-used-user id.*/

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val sessionKey: String = "current",
    val driverId: String?
)
