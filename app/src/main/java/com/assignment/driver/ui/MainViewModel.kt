package com.example.busdriver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.driver.domain.models.Route
import com.assignment.driver.domain.models.Trip
import com.assignment.driver.domain.usecases.EndTripUseCase
import com.assignment.driver.domain.usecases.GetRecentTripsUseCase
import com.assignment.driver.domain.usecases.GetRoutesUseCase
import com.assignment.driver.domain.usecases.LoginUseCase
import com.assignment.driver.domain.usecases.ObserveActiveTripUseCase
import com.assignment.driver.domain.usecases.StartTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val login: LoginUseCase,
    getRoutes: GetRoutesUseCase,
    observeActiveTrip: ObserveActiveTripUseCase,
    private val startTrip: StartTripUseCase,
    private val endTrip: EndTripUseCase,
    private val getRecentTrips: GetRecentTripsUseCase

) : ViewModel() {

    val sessionDriver = login.sessionDriver()

    private val _toasts = Channel<String>(Channel.BUFFERED)
    val toasts = _toasts.receiveAsFlow()

    private val _loginStatus = MutableStateFlow("Not logged in")
    val loginStatus: StateFlow<String> = _loginStatus.asStateFlow()

    val routes: StateFlow<List<Route>> =
        getRoutes.observeRoutes()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeTrip: StateFlow<Trip?> =
        observeActiveTrip.observe()
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _recentTrips = MutableStateFlow<List<Trip>>(emptyList())
    val recentTrips = _recentTrips.asStateFlow()

    init {
        // Load once; you can also refresh on lifecycle events
        viewModelScope.launch {
            _recentTrips.value = runCatching { getRecentTrips.get(20) }.getOrDefault(emptyList())
        }
    }

    fun refreshRecentTrips() {
        viewModelScope.launch {
            _recentTrips.value = runCatching { getRecentTrips.get(20) }.getOrDefault(emptyList())
        }
    }

    fun login(username: String) {
        viewModelScope.launch {
            val result = login.loginOffline(username)
            _loginStatus.value = result.fold(
                onSuccess = { "Logged in as ${it.name} (${it.id})" },
                onFailure = { "Login failed: ${it.message}" }
            )
        }
    }

    fun startTripFor(routeId: String) {
        viewModelScope.launch {
            if (sessionDriver.value == null) {
                _toasts.send("Please log in to start a trip.")
                return@launch
            }
            val trip = runCatching { startTrip.start(routeId) }
                .onFailure { _toasts.send(it.message ?: "Unable to start trip") }
                .getOrNull() ?: return@launch

            _events.send(UiEvent.StartService(trip.id))
        }
    }

    fun endActiveTrip() {
        viewModelScope.launch {
            if (sessionDriver.value == null) {
                _toasts.send("Please log in to end a trip.")
                return@launch
            }
            val trip = activeTrip.value ?: return@launch
            endTrip.end(trip.id)
            refreshRecentTrips()
            _events.send(UiEvent.StopService)
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { login.logout() }
                .onSuccess { _toasts.send("Logged out") }
                .onFailure { _toasts.send("Logout failed: ${it.message}") }
        }
    }
}

sealed interface UiEvent {
    data class StartService(val tripId: String) : UiEvent
    data object StopService : UiEvent
}
