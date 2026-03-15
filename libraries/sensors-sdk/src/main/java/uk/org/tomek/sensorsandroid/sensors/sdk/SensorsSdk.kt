package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultBleScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultLocationHandler
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultSensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultWifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.BleScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.LocationHandler
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.WifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

class SensorsSdk(
    context: Context,
) {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)
    private val wifiScanner: WifiScanner = DefaultWifiScanner(context)
    private val bleScanner: BleScanner = DefaultBleScanner(context)
    val locationHandler: LocationHandler = DefaultLocationHandler(context)

    val sensorDataFlow: Flow<SensorData> = sensorsListener.sensorDataFlow
    val wifiDataFlow: Flow<WifiData> = wifiScanner.wifiDataFlow
    val bleDataFlow: Flow<BleData> = bleScanner.bleDataFlow

    fun startSensors() {
        sensorsListener.startListening()
    }

    fun stopSensors() {
        sensorsListener.stopListening()
    }

    fun startWifiScanning(): Result<Unit> {
        return wifiScanner.startScanning()
    }

    fun stopWifiScanning() {
        wifiScanner.stopScanning()
    }

    fun startBleScanning(): Result<Unit> {
        return bleScanner.startScanning()
    }

    fun stopBleScanning() {
        bleScanner.stopScanning()
    }

    fun init() {
        startSensors()
        startWifiScanning()
        startBleScanning()
    }

    fun stopListening() {
        sensorsListener.stopListening()
        wifiScanner.stopScanning()
        bleScanner.stopScanning()
        stopSensors()
    }
}
