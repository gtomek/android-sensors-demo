package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData

interface SensorsRepository {

    val sensorDataFlow: Flow<SensorData>
    fun startSensors()

    fun stopLeasingSensors()
}