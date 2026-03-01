package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData

interface SensorsListener {

    val sensorDataFlow : Flow<SensorData>

    fun startListening()
    fun stopListening()
}