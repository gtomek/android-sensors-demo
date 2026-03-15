package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

interface WifiScanRepository {

    val wifiDataFlow: Flow<WifiData>

    fun startScanning(): Result<Unit>
    fun stopScanning()
}
