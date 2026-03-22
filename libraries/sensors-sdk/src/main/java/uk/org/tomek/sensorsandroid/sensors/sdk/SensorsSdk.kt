package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import android.location.Location
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkConfig
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkResult

interface SensorsSdk {
    val scanResults: Flow<SensorsSdkResult>
    fun start()
    fun stop()
    fun getDeviceInformation(): DeviceInfo
    fun getLastKnownLocation(): Result<Location>

    companion object {
        fun init(context: Context, config: SensorsSdkConfig = SensorsSdkConfig()) : SensorsSdk =
            SensorsSdkImplementation(context, config)
    }
}
