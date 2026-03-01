package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultSensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData

class SensorsSdk(
    context: Context,
) {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)

    val sensorDataFlow: Flow<SensorData> = sensorsListener.sensorDataFlow

    fun init() {
        sensorsListener.startListening()
    }

    fun stopListening() {
        sensorsListener.stopListening()
    }
}
