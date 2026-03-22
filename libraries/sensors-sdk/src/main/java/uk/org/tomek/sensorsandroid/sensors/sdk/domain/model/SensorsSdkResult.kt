package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

sealed interface SensorsSdkResult {
    data class SuccessEvent(val message: String) : SensorsSdkResult
    data class Error(val message: String) : SensorsSdkResult
}
