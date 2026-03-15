package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData

interface MobileNetworksRepository {
    val mobileNetworkDataFlow: Flow<MobileNetworkData>

    fun startScanning(): Result<Unit>
    fun stopScanning()
}
