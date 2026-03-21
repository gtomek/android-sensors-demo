package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultActivityRecognizer
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultBarometerCollector
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultBleScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultDeviceInformation
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultLocationHandler
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultMobileNetworksScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultSensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultWifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.ActivityRecognizer
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.BarometerCollector
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.BleScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.DeviceInformation
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.LocationHandler
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.MobileNetworksScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.WifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BarometerData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

class SensorsSdk(
    context: Context,
) {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)
    private val wifiScanner: WifiScanner = DefaultWifiScanner(context)
    private val bleScanner: BleScanner = DefaultBleScanner(context)
    private val mobileNetworksScanner: MobileNetworksScanner = DefaultMobileNetworksScanner(context)
    private val barometerCollector: BarometerCollector = DefaultBarometerCollector(context)
    private val activityRecognizer: ActivityRecognizer = DefaultActivityRecognizer(context)
    val locationHandler: LocationHandler = DefaultLocationHandler(context)

    val deviceInformation: DeviceInformation = DefaultDeviceInformation(context)

    val sensorDataFlow: Flow<SensorData> = sensorsListener.sensorDataFlow
    val wifiDataFlow: Flow<WifiData> = wifiScanner.wifiDataFlow
    val bleDataFlow: Flow<BleData> = bleScanner.bleDataFlow
    val mobileNetworkDataFlow: Flow<MobileNetworkData> = mobileNetworksScanner.mobileNetworkDataFlow
    val barometerDataFlow: Flow<BarometerData> = barometerCollector.barometerDataFlow
    val activityDataFlow: Flow<ActivityData> = activityRecognizer.activityDataFlow

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

    fun startMobileNetworksScanning(): Result<Unit> {
        return mobileNetworksScanner.startScanning()
    }

    fun stopMobileNetworksScanning() {
        mobileNetworksScanner.stopScanning()
    }

    fun startBarometerListening(): Result<Unit> {
        return barometerCollector.startListening()
    }

    fun stopBarometerListening() {
        barometerCollector.stopListening()
    }

    fun startActivityRecognition(): Result<Unit> {
        return activityRecognizer.startListening()
    }

    fun stopActivityRecognition() {
        activityRecognizer.stopListening()
    }

    fun init() {
        startSensors()
        startWifiScanning()
        startBleScanning()
        startMobileNetworksScanning()
        startBarometerListening()
        startActivityRecognition()
    }

    fun stopListening() {
        sensorsListener.stopListening()
        wifiScanner.stopScanning()
        bleScanner.stopScanning()
        mobileNetworksScanner.stopScanning()
        barometerCollector.stopListening()
        activityRecognizer.stopListening()
        stopSensors()
        stopMobileNetworksScanning()
    }
}
