package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.BleScanRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData

class BleScanRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : BleScanRepository {
    override val bleDataFlow: Flow<BleData> = sensorsSdk.bleDataFlow

    override fun startScanning() {
        sensorsSdk.startBleScanning()
    }

    override fun stopScanning() {
        sensorsSdk.stopBleScanning()
    }
}
