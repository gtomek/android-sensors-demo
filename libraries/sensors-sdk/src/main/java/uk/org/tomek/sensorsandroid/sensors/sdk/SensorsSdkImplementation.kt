package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
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
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkConfig
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkResult

internal class SensorsSdkImplementation(
    context: Context,
    private val config: SensorsSdkConfig = SensorsSdkConfig()
) : SensorsSdk {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)
    private val wifiScanner: WifiScanner = DefaultWifiScanner(context)
    private val bleScanner: BleScanner = DefaultBleScanner(context)
    private val mobileNetworksScanner: MobileNetworksScanner = DefaultMobileNetworksScanner(context)
    private val barometerCollector: BarometerCollector = DefaultBarometerCollector(context)
    private val activityRecognizer: ActivityRecognizer = DefaultActivityRecognizer(context)
    private val locationHandler: LocationHandler = DefaultLocationHandler(context)

    private val deviceInformation: DeviceInformation = DefaultDeviceInformation(context)

    override val scanResults: Flow<SensorsSdkResult> = merge(
        sensorsListener.sensorDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = it, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = null, activityData = null) },
        wifiScanner.wifiDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = it, bleData = null, mobileNetworkData = null, barometerData = null, activityData = null) },
        bleScanner.bleDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = it, mobileNetworkData = null, barometerData = null, activityData = null) },
        mobileNetworksScanner.mobileNetworkDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = it, barometerData = null, activityData = null) },
        barometerCollector.barometerDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = it, activityData = null) },
        activityRecognizer.activityDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = null, activityData = it) }
    )

    override fun start() {
        if (config.isSensorDataEnabled) sensorsListener.startListening()
        if (config.isWifiScanningEnabled) wifiScanner.startScanning()
        if (config.isBleScanningEnabled) bleScanner.startScanning()
        if (config.isMobileNetworkScanningEnabled) mobileNetworksScanner.startScanning()
        if (config.isBarometerListeningEnabled) barometerCollector.startListening()
        if (config.isActivityRecognitionEnabled) activityRecognizer.startListening()
    }

    override fun stop() {
        sensorsListener.stopListening()
        wifiScanner.stopScanning()
        bleScanner.stopScanning()
        mobileNetworksScanner.stopScanning()
        barometerCollector.stopListening()
        activityRecognizer.stopListening()
    }

    override fun getDeviceInformation(): DeviceInfo {
        return deviceInformation.getDeviceInformation()
    }

    override fun getLastKnownLocation(): Result<Location> {
        return locationHandler.getLastKnownLocation()
    }
}
