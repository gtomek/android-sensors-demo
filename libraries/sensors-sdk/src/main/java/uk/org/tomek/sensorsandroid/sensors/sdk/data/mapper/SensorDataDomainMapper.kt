package uk.org.tomek.sensorsandroid.sensors.sdk.data.mapper

import android.hardware.SensorEvent
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData

import kotlin.time.Instant

internal class SensorDataDomainMapper {
    fun toDomain(event: SensorEvent): SensorData =
        SensorData(
            sensorId = event.sensor.id,
            sensorType = event.sensor.type,
            sensorName = event.sensor.name,
            sensorValues = event.values.toList(),
            sensorTimestamp = Instant.fromEpochMilliseconds(event.timestamp),
            timestamp = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
        )
}
