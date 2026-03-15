package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.BarometerRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BarometerData

class BarometerRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : BarometerRepository {
    override val barometerDataFlow: Flow<BarometerData> = sensorsSdk.barometerDataFlow

    override fun startListening(): Result<Unit> {
        return sensorsSdk.startBarometerListening()
    }

    override fun stopListening() {
        sensorsSdk.stopBarometerListening()
    }
}
