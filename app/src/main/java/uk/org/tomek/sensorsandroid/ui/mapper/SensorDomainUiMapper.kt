package uk.org.tomek.sensorsandroid.ui.mapper

import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.WifiData
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
}
