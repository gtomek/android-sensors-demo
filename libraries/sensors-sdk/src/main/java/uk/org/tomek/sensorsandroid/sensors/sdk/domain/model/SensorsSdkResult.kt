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
    sealed class Error(open val message: String) : SensorsSdkResult {
        data class GenericError(override val message: String) : Error(message)
        data class PermissionError(val permissions: List<String>) : Error("Missing permissions: ${permissions.joinToString()}")
    }
}
