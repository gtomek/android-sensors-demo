package uk.org.tomek.sensorsandroid.ui.mapper

import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BeaconInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BleData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.CellInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData
import uk.org.tomek.sensorsandroid.ui.model.BleDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.CellInfoUiModel
import uk.org.tomek.sensorsandroid.ui.model.MobileNetworkDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.WifiDataUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SensorDomainUiMapper {

    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    fun toUi(sensorData: SensorData): SensorDataUiModel = SensorDataUiModel(
        sensorType = sensorData.sensorType,
        sensorStringType = "Type ${sensorData.sensorType} ${sensorData.sensorStringType}",
        sensorName = sensorData.sensorName,
        sensorTimestamp = sensorData.timestamp,
        sensorValues = sensorData.sensorValues
    )

    fun toUi(wifiData: WifiData): WifiDataUiModel = WifiDataUiModel(
        bssid = wifiData.bssid,
        ssid = wifiData.ssid,
        rssi = wifiData.rssi,
        frequency = wifiData.frequency,
        capabilities = wifiData.capabilities,
        distance = wifiData.distanceMm?.let { "${it / 1000.0}m (±${(wifiData.distanceStdDevMm ?: 0) / 1000.0}m)" },
        timestamp = dateFormat.format(Date(wifiData.timestamp))
    )

    fun toUi(bleData: BleData): BleDataUiModel = BleDataUiModel(
        deviceAddress = bleData.deviceAddress,
        deviceName = bleData.deviceName,
        rssi = bleData.rssi,
        txPower = bleData.txPower,
        beaconInfo = formatBeaconInfo(bleData.beaconInfo),
        timestamp = dateFormat.format(Date(bleData.timestamp))
    )

    fun toUi(mobileNetworkData: MobileNetworkData): MobileNetworkDataUiModel = MobileNetworkDataUiModel(
        timestamp = dateFormat.format(Date(mobileNetworkData.timestamp)),
        primaryCell = mobileNetworkData.primaryCell?.let { toUi(it) },
        neighboringCells = mobileNetworkData.neighboringCells.map { toUi(it) }
    )

    private fun toUi(cellInfo: CellInfo): CellInfoUiModel = CellInfoUiModel(
        type = cellInfo.type,
        cellId = cellInfo.cellId?.toString() ?: "N/A",
        lacTac = cellInfo.lac?.toString() ?: "N/A",
        mccMnc = "${cellInfo.mcc ?: "N/A"}/${cellInfo.mnc ?: "N/A"}",
        signalStrength = cellInfo.signalStrength?.let { "$it dBm" } ?: "N/A",
        timingAdvance = cellInfo.timingAdvance?.toString() ?: "N/A"
    )

    private fun formatBeaconInfo(info: BeaconInfo?): String? {
        return when (info) {
            is BeaconInfo.IBeacon -> "iBeacon: UUID=${info.proximityUuid}, Major=${info.major}, Minor=${info.minor}, TxPower=${info.txPower}"
            is BeaconInfo.Eddystone.Uid -> "Eddystone-UID: Namespace=${info.namespace}, Instance=${info.instance}"
            is BeaconInfo.Eddystone.Url -> "Eddystone-URL: ${info.url}"
            is BeaconInfo.Eddystone.Tlm -> "Eddystone-TLM: v${info.version}, ${info.batteryVoltage}mV, ${info.temperature}°C, PDU=${info.pduCount}, Uptime=${info.uptime}s"
            null -> null
        }
    }
}
