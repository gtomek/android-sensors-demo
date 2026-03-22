package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

sealed interface SensorsSdkResult {
    data class SuccessEvent(
        val sensor: SensorData?,
        val wifiData: WifiData?,
        val bleData: BleData?,
        val mobileNetworkData: MobileNetworkData?,
        val barometerData: BarometerData?,
        val activityData: ActivityData?,
    ) : SensorsSdkResult
    data class Error(val message: String) : SensorsSdkResult
}
