package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.BleScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BeaconInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData
import java.nio.ByteBuffer
import java.util.UUID
import androidx.core.util.size

class DefaultBleScanner(private val context: Context) : BleScanner {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _bleDataFlow = MutableSharedFlow<BleData>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val bleDataFlow = _bleDataFlow.asSharedFlow()

    private var scanStartTime: Long = 0

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            processScanResult(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            results.forEach { processScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("BLE Scan failed with error code: $errorCode")
        }
    }

    @SuppressLint("MissingPermission")
    override fun startScanning(): Result<Unit> {
        if (!hasRequiredPermissions()) {
            return Result.failure(SecurityException("Missing required permissions for BLE scanning"))
        }

        val scanner = bluetoothAdapter?.bluetoothLeScanner
            ?: return Result.failure(IllegalStateException("BLE Scanner not available"))

        scanStartTime = SystemClock.elapsedRealtime()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .apply {
                setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            }
            .build()

        val filters = listOf<ScanFilter>() // Scan for all

        return try {
            scanner.startScan(filters, settings, scanCallback)
            Timber.d("BLE Scanning started")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val scanPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            val connectPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            if (!scanPermission || !connectPermission) return false
        }
        
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun stopScanning() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        Timber.d("BLE Scanning stopped")
    }

    private fun processScanResult(result: ScanResult) {
        val now = System.currentTimeMillis()
        val record = result.scanRecord ?: return

        val manufacturerData = mutableMapOf<Int, ByteArray>()
        val sparseManufacturerData = record.manufacturerSpecificData
        for (i in 0 until sparseManufacturerData.size) {
            manufacturerData[sparseManufacturerData.keyAt(i)] = sparseManufacturerData.valueAt(i)
        }

        val serviceUuids = record.serviceUuids?.map { it.toString() } ?: emptyList()

        val beaconInfo = detectBeacon(record.bytes)

        @SuppressLint("MissingPermission")
        val bleData = BleData(
            timestamp = now,
            deviceAddress = result.device.address,
            deviceName = if (hasRequiredPermissions()) result.device.name else null,
            rssi = result.rssi,
            txPower = result.txPower,
            manufacturerData = manufacturerData,
            serviceUuids = serviceUuids,
            advertisingIntervalMillis = null, // Not directly available in ScanResult
            scanMode = ScanSettings.SCAN_MODE_LOW_LATENCY,
            scanDurationMillis = SystemClock.elapsedRealtime() - scanStartTime,
            beaconInfo = beaconInfo
        )

        _bleDataFlow.tryEmit(bleData)
    }

    private fun detectBeacon(scanRecord: ByteArray): BeaconInfo? {
        // Simple manual parsing of common beacon formats
        if (scanRecord.size < 30) return null

        // iBeacon detection (Apple Manufacturer ID: 0x004C)
        // Data usually starts at index 5 or 7 depending on flags
        // Pattern: [0x02, 0x15, UUID (16 bytes), Major (2 bytes), Minor (2 bytes), TxPower (1 byte)]
        for (i in 0 until scanRecord.size - 23) {
            if (scanRecord[i].toInt() == 0x02 && scanRecord[i + 1].toInt() == 0x15) {
                val uuid = bytesToUuidString(scanRecord.sliceArray(i + 2 until i + 18))
                val major = (scanRecord[i + 18].toInt() and 0xff shl 8) or (scanRecord[i + 19].toInt() and 0xff)
                val minor = (scanRecord[i + 20].toInt() and 0xff shl 8) or (scanRecord[i + 21].toInt() and 0xff)
                val txPower = scanRecord[i + 22].toInt()
                return BeaconInfo.IBeacon(uuid, major, minor, txPower)
            }
        }

        // Eddystone detection (Service UUID: 0xFEAA)
        // Eddystone-UID, Eddystone-URL, Eddystone-TLM
        for (i in 0 until scanRecord.size - 5) {
            // Look for Eddystone Service UUID 0xFEAA in Service Data
            if (scanRecord[i].toInt() == 0xAA && scanRecord[i + 1].toInt() == 0xFE) {
                val frameType = scanRecord[i + 2].toInt()
                when (frameType) {
                    0x00 -> { // UID
                        val namespace = bytesToHexString(scanRecord.sliceArray(i + 4 until i + 14))
                        val instance = bytesToHexString(scanRecord.sliceArray(i + 14 until i + 20))
                        return BeaconInfo.Eddystone.Uid(namespace, instance)
                    }
                    0x10 -> { // URL
                        val url = parseEddystoneUrl(scanRecord, i + 3)
                        return BeaconInfo.Eddystone.Url(url)
                    }
                    0x20 -> { // TLM
                        val version = scanRecord[i + 3].toInt()
                        val voltage = (scanRecord[i + 4].toInt() and 0xff shl 8) or (scanRecord[i + 5].toInt() and 0xff)
                        val temp = scanRecord[i + 6].toFloat() + (scanRecord[i + 7].toInt() and 0xff) / 256f
                        val pdu = ByteBuffer.wrap(scanRecord, i + 8, 4).int.toLong() and 0xFFFFFFFFL
                        val uptime = ByteBuffer.wrap(scanRecord, i + 12, 4).int.toLong() and 0xFFFFFFFFL
                        return BeaconInfo.Eddystone.Tlm(version, voltage, temp, pdu, uptime)
                    }
                }
            }
        }

        return null
    }

    private fun bytesToUuidString(bytes: ByteArray): String {
        val bb = ByteBuffer.wrap(bytes)
        return UUID(bb.long, bb.long).toString()
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun parseEddystoneUrl(bytes: ByteArray, offset: Int): String {
        val prefix = when (bytes[offset].toInt()) {
            0x00 -> "http://www."
            0x01 -> "https://www."
            0x02 -> "http://"
            0x03 -> "https://"
            else -> ""
        }
        val encodedUrl = bytes.sliceArray(offset + 1 until bytes.size).takeWhile { it.toInt() != 0 }.toByteArray()
        // Simplified URL decoding for Eddystone
        return prefix + String(encodedUrl)
    }
}
