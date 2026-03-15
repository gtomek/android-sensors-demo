package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class BarometerData(
    val timestamp: Long,
    val pressure: Float,
    val altitude: Float,
    val accuracy: Int
)
