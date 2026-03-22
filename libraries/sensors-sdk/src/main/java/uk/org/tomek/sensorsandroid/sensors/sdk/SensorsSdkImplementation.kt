package uk.org.tomek.sensorsandroid.sensors.sdk

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
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
    private val context: Context,
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

    private val _errorFlow = MutableSharedFlow<SensorsSdkResult.Error>(replay = 0)

    override val scanResults: Flow<SensorsSdkResult> = merge(
        _errorFlow,
        sensorsListener.sensorDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = it, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = null, activityData = null) },
        wifiScanner.wifiDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = it, bleData = null, mobileNetworkData = null, barometerData = null, activityData = null) },
        bleScanner.bleDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = it, mobileNetworkData = null, barometerData = null, activityData = null) },
        mobileNetworksScanner.mobileNetworkDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = it, barometerData = null, activityData = null) },
        barometerCollector.barometerDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = it, activityData = null) },
        activityRecognizer.activityDataFlow.map { SensorsSdkResult.SuccessEvent(sensor = null, wifiData = null, bleData = null, mobileNetworkData = null, barometerData = null, activityData = it) }
    ).onStart {
        val missingPermissions = getMissingPermissions()
        if (missingPermissions.isNotEmpty()) {
            emit(SensorsSdkResult.Error.PermissionError(missingPermissions))
        }
    }

    override fun start() {
        val missingPermissions = getMissingPermissions()
        if (missingPermissions.isNotEmpty()) {
            _errorFlow.tryEmit(SensorsSdkResult.Error.PermissionError(missingPermissions))
            return
        }

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
        val missingPermissions = getMissingPermissions()
        if (missingPermissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) ||
            missingPermissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            return Result.failure(SecurityException("Missing location permissions"))
        }
        return locationHandler.getLastKnownLocation()
    }

    private fun getMissingPermissions(): List<String> {
        val required = mutableListOf<String>()
        if (config.isWifiScanningEnabled || config.isBleScanningEnabled || config.isMobileNetworkScanningEnabled || config.isLocationScanningEnabled) {
            required.add(Manifest.permission.ACCESS_FINE_LOCATION)
            required.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (config.isWifiScanningEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            required.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        if (config.isBleScanningEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            required.add(Manifest.permission.BLUETOOTH_SCAN)
            required.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (config.isMobileNetworkScanningEnabled) {
            required.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (config.isActivityRecognitionEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            required.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        return required.distinct().filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }
}
