package uk.org.tomek.sensorsandroid.data

import timber.log.Timber
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorListener

class SensorsRepositoryDefault(
    private val sensorListener: SensorListener,
    private val sensorDataMapper: SensorDataMapper,
) : SensorsRepository {
    override fun startSensors(): Boolean {
        sensorListener.startListening()
        Timber.v("Sensors started!")
        return true
    }

}