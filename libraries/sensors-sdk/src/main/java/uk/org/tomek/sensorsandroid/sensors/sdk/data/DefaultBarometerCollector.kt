package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.BarometerCollector
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BarometerData

internal class DefaultBarometerCollector(
    context: Context
) : SensorEventListener, BarometerCollector {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    private val _barometerDataFlow = MutableSharedFlow<BarometerData>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val barometerDataFlow: SharedFlow<BarometerData> = _barometerDataFlow.asSharedFlow()

    override fun startListening(): Result<Unit> {
        if (barometerSensor == null) {
            Timber.w("Barometer sensor not available")
            return Result.failure(IllegalStateException("Barometer sensor not available"))
        }
        val supported = sensorManager.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        return if (supported) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Failed to register barometer listener"))
        }
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_PRESSURE) return

        val pressure = event.values[0]
        val altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)
        val timestamp = System.currentTimeMillis()
        
        val data = BarometerData(
            timestamp = timestamp,
            pressure = pressure,
            altitude = altitude,
            accuracy = event.accuracy
        )
        _barometerDataFlow.tryEmit(data)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.d("Barometer accuracy changed: $accuracy")
    }
}
