package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class SensorsSdkConfig(
    val isSensorDataEnabled: Boolean = true,
    val isWifiScanningEnabled: Boolean = true,
    val isBleScanningEnabled: Boolean = true,
    val isMobileNetworkScanningEnabled: Boolean = true,
    val isBarometerListeningEnabled: Boolean = true,
    val isActivityRecognitionEnabled: Boolean = true,
    val isLocationScanningEnabled: Boolean = true,
)
