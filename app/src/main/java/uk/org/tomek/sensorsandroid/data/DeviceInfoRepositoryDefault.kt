package uk.org.tomek.sensorsandroid.data

import uk.org.tomek.sensorsandroid.domain.DeviceInfoRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo

class DeviceInfoRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : DeviceInfoRepository {
    override fun getDeviceInfo(): DeviceInfo {
        return sensorsSdk.getDeviceInformation()
    }
}
