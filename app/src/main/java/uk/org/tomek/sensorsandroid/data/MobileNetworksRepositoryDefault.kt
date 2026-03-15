package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.MobileNetworksRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData

class MobileNetworksRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : MobileNetworksRepository {
    override val mobileNetworkDataFlow: Flow<MobileNetworkData> = sensorsSdk.mobileNetworkDataFlow

    override fun startScanning(): Result<Unit> {
        return sensorsSdk.startMobileNetworksScanning()
    }

    override fun stopScanning() {
        sensorsSdk.stopMobileNetworksScanning()
    }
}
