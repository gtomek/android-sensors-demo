package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

interface WifiScanner {

    val wifiDataFlow: Flow<WifiData>

    fun startScanning()
    fun stopScanning()
}