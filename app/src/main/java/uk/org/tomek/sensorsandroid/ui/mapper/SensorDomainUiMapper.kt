package uk.org.tomek.sensorsandroid.ui.mapper

import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel

class SensorDomainUiMapper {

    fun toUi(sensorData: SensorData): SensorDataUiModel = SensorDataUiModel(
        sensorType = sensorData.sensorType,
        sensorStringType = "Type ${sensorData.sensorType} ${sensorData.sensorStringType}",
        sensorName = sensorData.sensorName,
        sensorTimestamp = sensorData.timestamp,
        sensorValues = sensorData.sensorValues
    )
}
