package com.assignment.driver.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assignment.driver.R
import com.assignment.driver.databinding.ActivityMainBinding
import com.assignment.driver.domain.models.Route
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.models.TripStatus
import com.assignment.driver.domain.usecases.CancelTripSyncUseCase
import com.assignment.driver.service.LocationTrackingService
import com.assignment.driver.util.PermissionHelper
import com.assignment.driver.util.formatTs
import com.example.busdriver.ui.MainViewModel
import com.example.busdriver.ui.UiEvent
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()

    @Inject lateinit var cancelTripSync: CancelTripSyncUseCase

    private val routesAdapter = RoutesAdapter { route ->
        // Require session in VM; this only gets clickable when logged in anyway
        vm.startTripFor(route.id)
    }

    private val recentAdapter = RecentTripsAdapter { trip ->
        val i = Intent(this, TripDetailsActivity::class.java)
        i.putExtra("tripId", trip.id)
        startActivity(i)
    }

    // Enforce mandatory permissions before starting tracking
    private var pendingTripId: String? = null

    // Notifications (API 33+)
    private val reqNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted && Build.VERSION.SDK_INT >= 33) {
            showError("Notification permission is required to show trip tracking notification.")
            pendingTripId = null
            return@registerForActivityResult
        }
        requestForegroundIfNeeded()
    }

    // Foreground location
    private val reqForeground = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = PermissionHelper.foregroundPerms.all { p -> result[p] == true }
        if (!allGranted) {
            showError("Location permission is required to start trip tracking.")
            pendingTripId = null
            return@registerForActivityResult
        }
        requestBackgroundIfNeeded()
    }

    // Background location (Android 10+)
    private val reqBackground = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showError("Background location is required to continue tracking when the app is not in the foreground.")
            pendingTripId = null
            return@registerForActivityResult
        }
        // All mandatory granted
        startServiceNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lists
        binding.routesList.adapter = routesAdapter
        binding.recentTripsList.adapter = recentAdapter
        binding.recentTripsList.layoutManager = LinearLayoutManager(this)

        // Login
        binding.loginBtn.setOnClickListener {
            val username = binding.eTxtUsername.text?.toString().orEmpty()
            vm.login(username)
        }
        binding.eTxtUsername.addTextChangedListener {
            binding.tilPhoneNumber.error = null
        }

        // Logout
        binding.logoutBtn.setOnClickListener {
            // Stop tracking service if running
            LocationTrackingService.stop(this)
            // Optionally cancel queued trip sync work
            cancelTripSync.cancelAll()
            // Clear session
            vm.logout()
        }

        // End trip
        binding.endTripBtn.setOnClickListener { vm.endActiveTrip() }

        // Collect state
        lifecycleScope.launch {
            vm.loginStatus.collectLatest {
                // If login failed, show error near input
                if (it.contains("Login failed", true)) {
                    binding.tilPhoneNumber.error = it
                    binding.loginStatus.text = ""
                } else {
                    binding.tilPhoneNumber.error = null
                    binding.loginStatus.text = it
                }
            }
        }
        lifecycleScope.launch {
            vm.routes.collectLatest { routesAdapter.submitList(it) }
        }
        lifecycleScope.launch {
            vm.recentTrips.collect { list -> recentAdapter.submitList(list) }
        }
        lifecycleScope.launch {
            vm.activeTrip.collectLatest { trip ->
                binding.activeTripStatus.text = if (trip == null) "" else "Active trip: ${trip.id}"
                binding.endTripBtn.isEnabled = trip != null
                if(trip==null){
                    binding.activeTripStatus.text=resources.getString(R.string.no_active_trips)
                }
            }
        }

        // CRITICAL: Only show app data/actions if logged in
        lifecycleScope.launch {
            vm.sessionDriver.collectLatest { driver ->
                val loggedIn = driver != null
                setLoggedInUi(loggedIn, driver?.name, driver?.id)
            }
        }

        lifecycleScope.launch {
            vm.toasts.collectLatest { msg -> binding.loginStatus.text = msg }
        }

        lifecycleScope.launch {
            vm.events.collectLatest { event ->
                when (event) {
                    is UiEvent.StartService -> startTripWithMandatoryPermissions(event.tripId)
                    UiEvent.StopService -> {
                        LocationTrackingService.stop(this@MainActivity)
                        binding.recentTripsList.post {
                            binding.recentTripsList.scrollToPosition(0)
                        }
                    }
                }
            }
        }
    }

    // Enforce mandatory permissions (Notifications 33+, Foreground, Background)
    private fun startTripWithMandatoryPermissions(tripId: String) {
        pendingTripId = tripId
        // Notifications first (API 33+)
        val notifPerm = PermissionHelper.notificationPerm()
        if (notifPerm != null &&
            ContextCompat.checkSelfPermission(this, notifPerm) != PackageManager.PERMISSION_GRANTED
        ) {
            reqNotifications.launch(notifPerm)
            return
        }
        // Foreground next
        requestForegroundIfNeeded()
    }

    private fun requestForegroundIfNeeded() {
        val missingForeground = PermissionHelper.foregroundPerms.any {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingForeground) {
            reqForeground.launch(PermissionHelper.foregroundPerms)
        } else {
            // Background mandatory (minSdk >= 29 in this project)
            requestBackgroundIfNeeded()
        }
    }

    private fun requestBackgroundIfNeeded() {
        val bg = PermissionHelper.backgroundPerm()
        if (bg == null) {
            showError("Background location permission not available on this device.")
            pendingTripId = null
            return
        }
        if (ContextCompat.checkSelfPermission(this, bg) != PackageManager.PERMISSION_GRANTED) {
            reqBackground.launch(bg)
        } else {
            startServiceNow()
        }
    }

    private fun startServiceNow() {
        val tId = pendingTripId ?: return
        LocationTrackingService.start(this, tId)
        pendingTripId = null
        Snackbar.make(binding.root, "Tracking started", Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    // Strict UI gating: only show app data when logged in
    private fun setLoggedInUi(isLoggedIn: Boolean, name: String?, id: String?) {
        if (isLoggedIn) {
            // Hide login controls
            binding.tilPhoneNumber.visibility = GONE
            binding.loginBtn.visibility = GONE
            // Show data/actions
            binding.logoutBtn.visibility = VISIBLE
            binding.loginStatus.visibility = VISIBLE
            binding.routesHeader.visibility = VISIBLE
            binding.routesList.visibility = VISIBLE
            binding.recentTripsHeader.visibility = VISIBLE
            binding.recentTripsList.visibility = VISIBLE
            binding.activeTripStatus.visibility = VISIBLE
            binding.endTripBtn.visibility = VISIBLE

            binding.loginStatus.text = "Logged in as ${name ?: "-"} (${id ?: "-"})"
        } else {
            // Show only login controls
            binding.tilPhoneNumber.visibility = VISIBLE
            binding.loginBtn.visibility = VISIBLE

            // Hide everything else
            binding.logoutBtn.visibility = GONE
            binding.loginStatus.visibility = GONE
            binding.routesHeader.visibility = GONE
            binding.routesList.visibility = GONE
            binding.recentTripsHeader.visibility = GONE
            binding.recentTripsList.visibility = GONE
            binding.activeTripStatus.visibility = GONE
            binding.endTripBtn.visibility = GONE
        }
    }
}

private class RoutesAdapter(
    private val onClick: (Route) -> Unit
) : ListAdapter<Route, RouteVH>(RouteDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false) as CardView
        return RouteVH(v, onClick)
    }

    override fun onBindViewHolder(holder: RouteVH, position: Int) {
        holder.bind(getItem(position))
    }
}

private class RouteVH(
    private val v: CardView,
    private val onClick: (Route) -> Unit
) : RecyclerView.ViewHolder(v) {
    fun bind(item: Route) {
        val tv=v.findViewById<MaterialTextView>(R.id.routeName)
        tv.text = item.name
        tv.setOnClickListener { onClick(item) }
    }
}

private class RecentTripsAdapter(
    private val onClick: (Trip) -> Unit
) : ListAdapter<Trip, RecentTripVH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentTripVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return RecentTripVH(parent.context,v as View, onClick)
    }

    override fun onBindViewHolder(holder: RecentTripVH, position: Int) =
        holder.bind(getItem(position))
}

private class RecentTripVH(
    private val context: Context,
    itemView: View,
    private val onClick: (Trip) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val title = itemView.findViewById<MaterialTextView>(R.id.tripTitle)
    private val subtitle = itemView.findViewById<MaterialTextView>(R.id.tripSubtitle)
    private val times = itemView.findViewById<MaterialTextView>(R.id.tripTimes)
    private val card = itemView.findViewById<LinearLayout>(R.id.itemTripCard)

    @SuppressLint("SetTextI18n")
    fun bind(item: Trip) {
        // Title can be Trip ID (shortened) or Route ID; we keep it simple
        title.text = "Trip ${item.id.take(8)}"
        subtitle.text = "Route: ${item.routeId} • Status: ${item.status.name}"

        when (item.status.name) {
            TripStatus.COMPLETED.name -> {
                card.setBackgroundColor(getColor(context,R.color.completed))
            }
            TripStatus.ACTIVE.name -> {
                card.setBackgroundColor(getColor(context,R.color.ongoing))
            }
            TripStatus.SYNCED.name -> {
                card.setBackgroundColor(getColor(context,R.color.synced))
            }
        }

        val s = formatTs(item.startTime)
        val e = item.endTime?.let { formatTs(it) } ?: "-"
        times.text = "$s  —  $e"

        itemView.setOnClickListener { onClick(item) }
    }
}

private class Diff : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip) =
        oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Trip, newItem: Trip) =
        oldItem == newItem
}


private class RouteDiff : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Route, newItem: Route) = oldItem == newItem
}
