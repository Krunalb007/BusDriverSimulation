package com.assignment.driver.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun formatTs(millis: Long): String = sdf.format(Date(millis))
