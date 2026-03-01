package uk.org.tomek.sensorsandroid.ui.model

import kotlin.time.Instant

data class SensorDataUiModel(
    val sensorType: Int,
    val sensorStringType: String,
    val sensorName: String,
    val sensorTimestamp: Instant,
    val sensorValues: List<Float>,
)
