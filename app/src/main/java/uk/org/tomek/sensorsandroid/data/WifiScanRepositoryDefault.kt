package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.WifiScanRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

class WifiScanRepositoryDefault(
    private val sensorsSdk: SensorsSdk,
) : WifiScanRepository {
    override val wifiDataFlow: Flow<WifiData> = sensorsSdk.wifiDataFlow

    override fun startScanning() {
        sensorsSdk.init()
    }

    override fun stopScanning() {
        sensorsSdk.stopListening()
    }
}
