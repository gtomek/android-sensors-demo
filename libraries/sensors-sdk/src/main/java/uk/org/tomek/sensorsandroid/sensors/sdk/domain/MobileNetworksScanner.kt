package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData

internal interface MobileNetworksScanner {

    val mobileNetworkDataFlow: Flow<MobileNetworkData>

    fun startScanning(): Result<Unit>
    fun stopScanning()
}
