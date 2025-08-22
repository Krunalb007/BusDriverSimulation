package com.assignment.driver.domain.session

import com.assignment.driver.domain.models.Driver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _currentDriver = MutableStateFlow<Driver?>(null)
    val currentDriver: StateFlow<Driver?> = _currentDriver

    fun setDriver(driver: Driver?) {
        _currentDriver.value = driver
    }
}
