package uk.org.tomek.sensorsandroid.domain

import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo

interface DeviceInfoRepository {
    fun getDeviceInfo(): DeviceInfo
}
