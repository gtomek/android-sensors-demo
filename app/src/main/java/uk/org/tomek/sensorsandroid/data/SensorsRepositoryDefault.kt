package uk.org.tomek.sensorsandroid.data

import timber.log.Timber
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk

class SensorsRepositoryDefault(
    private val sensorsSdk: SensorsSdk,
    private val sensorDataMapper: SensorDataMapper,
) : SensorsRepository {

    override fun startSensors() {
        sensorsSdk.init()
        Timber.i("Sensors started!")
    }

    override fun stopLeasingSensors() {
        sensorsSdk.stopListening()
        Timber.i("Sensors stopped!")
    }
}