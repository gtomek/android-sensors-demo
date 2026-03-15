package uk.org.tomek.sensorsandroid.ui.model

data class WifiDataUiModel(
    val bssid: String,
    val ssid: String,
    val rssi: Int,
    val frequency: Int,
    val capabilities: String,
    val distance: String?,
    val timestamp: String
)
