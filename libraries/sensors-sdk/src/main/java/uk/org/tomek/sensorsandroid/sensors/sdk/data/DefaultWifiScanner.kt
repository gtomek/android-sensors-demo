package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.WifiScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData

internal class DefaultWifiScanner(private val context: Context) : WifiScanner {

    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val wifiRttManager = context.applicationContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as? WifiRttManager

    private val _wifiDataFlow = MutableSharedFlow<WifiData>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val wifiDataFlow = _wifiDataFlow.asSharedFlow()

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                processScanResults()
            } else {
                Timber.w("WiFi scan results not updated")
            }
        }
    }

    override fun startScanning(): Result<Unit> {
        if (!hasRequiredPermissions()) {
            return Result.failure(SecurityException("Missing required permissions for WiFi scanning"))
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        @Suppress("DEPRECATION")
        val success = wifiManager.startScan()
        return if (success) {
            Result.success(Unit)
        } else {
            processScanResults() // Even if startScan fails, we might still get results from a previous scan
            Result.failure(IllegalStateException("WiFi scan start failed"))
        }
    }

    override fun stopScanning() {
        try {
            context.unregisterReceiver(wifiScanReceiver)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Receiver not registered")
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val wifiState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            fineLocation && wifiState
        } else {
            fineLocation && wifiState
        }
    }

    @SuppressLint("MissingPermission")
    private fun processScanResults() {
        if (!hasRequiredPermissions()) return

        val results = wifiManager.scanResults
        val now = System.currentTimeMillis()
        val bootTime = now - SystemClock.elapsedRealtime()

        val wifiState = wifiManager.wifiState

        results.forEach { result ->
            val timestamp = bootTime + (result.timestamp / 1000)

            val scanLatency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                now - timestamp
            } else {
                null
            }

            @Suppress("DEPRECATION")
            val ssidValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.wifiSsid?.toString() ?: result.SSID
            } else {
                result.SSID
            }

            val wifiData = WifiData(
                timestamp = timestamp,
                bssid = result.BSSID,
                ssid = ssidValue ?: "",
                rssi = result.level,
                frequency = result.frequency,
                channelWidth = result.channelWidth,
                capabilities = result.capabilities,
                wifiState = wifiState,
                scanLatencyMillis = scanLatency
            )

            if (wifiRttManager != null && result.is80211mcResponder) {
                performRttRanging(result, wifiData)
            } else {
                _wifiDataFlow.tryEmit(wifiData)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun performRttRanging(scanResult: android.net.wifi.ScanResult, wifiData: WifiData) {
        if (wifiRttManager == null || !hasRequiredPermissions()) return

        val request = RangingRequest.Builder()
            .addAccessPoint(scanResult)
            .build()

        wifiRttManager.startRanging(request, context.mainExecutor, object : RangingResultCallback() {
            override fun onRangingFailure(code: Int) {
                Timber.w("RTT ranging failed with code $code for ${scanResult.BSSID}")
                _wifiDataFlow.tryEmit(wifiData)
            }

            override fun onRangingResults(results: List<RangingResult>) {
                val rttResult = results.firstOrNull { it.macAddress?.toString() == scanResult.BSSID }
                if (rttResult != null && rttResult.status == RangingResult.STATUS_SUCCESS) {
                    _wifiDataFlow.tryEmit(
                        wifiData.copy(
                            distanceMm = rttResult.distanceMm,
                            distanceStdDevMm = rttResult.distanceStdDevMm
                        )
                    )
                } else {
                    _wifiDataFlow.tryEmit(wifiData)
                }
            }
        })
    }
}
