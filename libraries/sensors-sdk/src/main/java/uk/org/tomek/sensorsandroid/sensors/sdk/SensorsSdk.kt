package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultSensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultWifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.WifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

class SensorsSdk(
    context: Context,
) {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)
    private val wifiScanner: WifiScanner = DefaultWifiScanner(context)

    val sensorDataFlow: Flow<SensorData> = sensorsListener.sensorDataFlow
    val wifiDataFlow: Flow<WifiData> = wifiScanner.wifiDataFlow

    fun startSensors() {
        sensorsListener.startListening()
    }

    fun stopSensors() {
        sensorsListener.stopListening()
    }

    fun startWifiScanning() {
        wifiScanner.startScanning()
    }

    fun stopWifiScanning() {
        wifiScanner.stopScanning()
    }

    fun init() {
        startSensors()
        startWifiScanning()
    }

    fun stopListening() {
        stopSensors()
        stopWifiScanning()
    }
}
