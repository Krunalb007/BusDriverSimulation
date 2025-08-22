package com.assignment.driver.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.assignment.driver.R
import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.domain.models.TripLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * LocationTrackingService
 *
 * Foreground service responsible for continuous location capture during an active trip.
 *
 * Responsibilities:
 * - Run in the foreground with a persistent notification while recording locations.
 * - Subscribe to FusedLocationProviderClient updates at the configured interval/priority.
 * - Persist each batch of locations into the local database via TripRepository.
 *
 * Lifecycle:
 * - start(context, tripId): starts the service for a specific trip.
 * - stop(context): stops the service and unsubscribes from location updates.
 * - onCreate(): initializes location client, notification channel, and foreground mode.
 * - onStartCommand(): receives the tripId and begins requesting location updates.
 * - onDestroy(): cleans up the callback and coroutine scope.
 *
 * Permissions:
 * - Requires foreground and background location permissions before being started.
 * - If started without proper permissions, the service will stop itself gracefully.
 *
 * Error handling:
 * - Exceptions while inserting points are caught and logged via Timber; service continues.
 */
@AndroidEntryPoint
class LocationTrackingService : Service() {

    companion object {
        const val CHANNEL_ID = "trip_tracking_channel"
        const val NOTIF_ID = 1001
        const val EXTRA_TRIP_ID = "extra_trip_id"

        /**
         * Start the foreground service for the given trip.
         */
        fun start(context: Context, tripId: String) {
            val i = Intent(context, LocationTrackingService::class.java)
                .putExtra(EXTRA_TRIP_ID, tripId)
            context.startForegroundService(i)
        }

        /**
         * Stop the foreground service if running.
         */
        fun stop(context: Context) {
            val i = Intent(context, LocationTrackingService::class.java)
            context.stopService(i)
        }
    }

    @Inject lateinit var tripRepo: TripRepository

    private lateinit var client: FusedLocationProviderClient
    private var serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var tripId: String? = null

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(this)
        createChannel()
        try {
            startForeground(NOTIF_ID, buildNotification())
        } catch (se: SecurityException) {
            // If somehow started without proper permissions, stop gracefully
            stopSelf()
            return
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tripId = intent?.getStringExtra(EXTRA_TRIP_ID)
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Create the notification channel for foreground operation.
     */
    private fun createChannel() {
        val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Trip Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        mgr.createNotificationChannel(channel)
    }

    /**
     * Build the ongoing foreground notification.
     */
    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bus_24)
            .setContentTitle("Trip in progress")
            .setContentText("Recording location")
            .setOngoing(true)
            .build()
    }

    /**
     * Receives batched location updates and persists them as TripLocation rows.
     */
    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val tId = tripId ?: return
            val points = result.locations.map { loc ->
                TripLocation(
                    id = null,
                    tripId = tId,
                    timestamp = loc.time,
                    lat = loc.latitude,
                    lng = loc.longitude,
                    accuracy = loc.accuracy,
                    speed = loc.speed,
                    bearing = loc.bearing
                )
            }
            serviceScope.launch {
                try {
                    tripRepo.addLocations(points)
                } catch (e: Throwable) {
                    Timber.w(e, "Failed to persist location batch")
                }
            }
        }
    }

    /**
     * Begin requesting periodic location updates from FusedLocationProviderClient.
     */
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            1_000L
        )
            .setMinUpdateIntervalMillis(1_000L)
            .build()

        try {
            client.requestLocationUpdates(request, callback, mainLooper)
        } catch (e: SecurityException) {
            Timber.w(e, "Missing location permission; stopping service")
            stopSelf()
        }
    }

    /**
     * Stop receiving location updates.
     */
    private fun stopLocationUpdates() {
        client.removeLocationUpdates(callback)
    }
}
