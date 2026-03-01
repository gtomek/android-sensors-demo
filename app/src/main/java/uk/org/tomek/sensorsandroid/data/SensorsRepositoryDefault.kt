package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData

class SensorsRepositoryDefault(
    private val sensorsSdk: SensorsSdk,
    private val sensorDataMapper: SensorDataMapper,
) : SensorsRepository {
    override val sensorDataFlow: Flow<SensorData> = sensorsSdk.sensorDataFlow

    override fun startSensors() {
        sensorsSdk.init()
        Timber.i("Sensors started!")
    }

    override fun stopLeasingSensors() {
        sensorsSdk.stopListening()
        Timber.i("Sensors stopped!")
    }
}