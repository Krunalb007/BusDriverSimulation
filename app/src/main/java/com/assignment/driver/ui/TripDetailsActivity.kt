package com.assignment.driver.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.assignment.driver.R
import com.assignment.driver.data.repository.TripRepository
import com.assignment.driver.databinding.ActivityTripDetailsBinding
import com.assignment.driver.util.formatTs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class TripDetailsActivity : AppCompatActivity() {

    @Inject lateinit var tripRepo: TripRepository
    private lateinit var binding: ActivityTripDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra("tripId")
        if (tripId.isNullOrEmpty()) {
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val trip = tripRepo.getTripById(tripId)
            val count = tripRepo.getLocationCount(tripId)
            val (first, last) = tripRepo.getLocationTimeRange(tripId)
            withContext(Dispatchers.Main) {
                if (trip == null) {
                    binding.summary.text = getString(R.string.trip_not_found)
                } else {
                    binding.summary.text = buildString {
                        appendLine("Trip ID: ${trip.id}")
                        appendLine("Route: ${trip.routeId}")
                        appendLine("Status: ${trip.status.name}")
                        appendLine("Start: ${formatTs(trip.startTime)}")
                        appendLine("End: ${trip.endTime?.let { formatTs(it) } ?: "-"}")
                        appendLine("Points: $count")
                        appendLine("First point at: ${first?.let { formatTs(it) } ?: "-"}")
                        appendLine("Last point at: ${last?.let { formatTs(it) } ?: "-"}")
                    }
                }
            }
        }
    }
}
