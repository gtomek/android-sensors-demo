package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import uk.org.tomek.sensorsandroid.sensors.sdk.data.DefaultSensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener

class SensorsSdk(
    context: Context,
) {
    private val sensorsListener: SensorsListener = DefaultSensorsListener(context)

    val sensorDataFlow: Flow<Unit> = MutableStateFlow<Unit>(Unit)

    fun init() {
        sensorsListener.startListening()
    }

    fun stopListening() {
        sensorsListener.stopListening()
    }
}
