package uk.org.tomek.sensorsandroid.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkResult

interface SensorsRepository {
    val scanResults: Flow<SensorsSdkResult>
    fun start()
    fun stop()
    fun getDeviceInfo(): DeviceInfo
    fun getLastKnownLocation(): Result<Location>
}
