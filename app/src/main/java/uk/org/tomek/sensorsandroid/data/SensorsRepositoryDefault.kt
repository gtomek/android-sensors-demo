package uk.org.tomek.sensorsandroid.data

import android.location.Location
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkResult

class SensorsRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : SensorsRepository {

    override val scanResults: Flow<SensorsSdkResult> = sensorsSdk.scanResults

    override fun start() {
        sensorsSdk.start()
    }

    override fun stop() {
        sensorsSdk.stop()
    }

    override fun getDeviceInfo(): DeviceInfo {
        return sensorsSdk.getDeviceInformation()
    }

    override fun getLastKnownLocation(): Result<Location> {
        return sensorsSdk.getLastKnownLocation()
    }
}
