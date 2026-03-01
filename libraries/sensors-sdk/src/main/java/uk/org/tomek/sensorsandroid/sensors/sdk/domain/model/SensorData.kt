package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

import kotlin.time.Instant

data class SensorData(
    val sensorId: Int,
    val sensorType: Int,
    val sensorName: String,
    val timestamp: Instant,
    val sensorValues: List<Float>
)