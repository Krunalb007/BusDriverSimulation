package com.assignment.driver.util

import android.Manifest
import android.os.Build

object PermissionHelper {
    val foregroundPerms = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun backgroundPerm(): String? =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

    fun notificationPerm(): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.POST_NOTIFICATIONS
        else null
}
