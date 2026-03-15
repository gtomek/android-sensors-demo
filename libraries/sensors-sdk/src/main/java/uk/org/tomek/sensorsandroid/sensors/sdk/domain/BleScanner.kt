package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData

/**
 * Bluetooth Low Energy (BLE) Beacons scanner.
 */
interface BleScanner {
    val bleDataFlow: Flow<BleData>

    fun startScanning(): Result<Unit>
    fun stopScanning()
}
