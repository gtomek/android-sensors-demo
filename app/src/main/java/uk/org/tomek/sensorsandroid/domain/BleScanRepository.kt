package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData

interface BleScanRepository {
    val bleDataFlow: Flow<BleData>
    fun startScanning(): Result<Unit>
    fun stopScanning()
}
