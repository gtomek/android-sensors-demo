package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo

internal interface DeviceInformation {

    fun getDeviceInformation(): DeviceInfo

}