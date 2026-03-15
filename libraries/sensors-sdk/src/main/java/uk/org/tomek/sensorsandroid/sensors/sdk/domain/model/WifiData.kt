package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class WifiData(
    val timestamp: Long,
    val bssid: String,
    val ssid: String,
    val rssi: Int,
    val frequency: Int,
    val channelWidth: Int,
    val capabilities: String,
    val distanceMm: Int? = null,
    val distanceStdDevMm: Int? = null,
    val scanLatencyMillis: Long? = null,
    val wifiState: Int? = null
)
