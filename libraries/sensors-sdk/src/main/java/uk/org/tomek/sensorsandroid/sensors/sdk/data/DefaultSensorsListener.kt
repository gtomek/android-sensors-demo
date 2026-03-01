package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.data.mapper.SensorDataDomainMapper
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import kotlin.time.Duration.Companion.milliseconds

internal class DefaultSensorsListener(
    context: Context,
    private val sensorDataDomainMapper: SensorDataDomainMapper = SensorDataDomainMapper(),
) : SensorEventListener, SensorsListener {

    private val _sensorDataFlow = MutableSharedFlow<SensorData>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val sensorDataFlow: SharedFlow<SensorData> = _sensorDataFlow.asSharedFlow()

    private fun onSensorData(data: SensorData) {
        Timber.d("Sensor data: ${data.sensorName} [${data.sensorType}] ${data.sensorValues} ${data.sensorTimestamp}")
        _sensorDataFlow.tryEmit(data)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
    private val lastProcessedTimes = mutableMapOf<Int, Long>()

    override fun startListening() {
        sensors.forEach { sensor ->
            // Request 0.5 second delay (500,000 microseconds)
            val supported = sensorManager.registerListener(this, sensor, 500.milliseconds.inWholeMicroseconds.toInt())
            if (!supported) {
                Timber.w("Sensor ${sensor.name} not supported")
            }
        }
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
        lastProcessedTimes.clear()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val currentTime = SystemClock.elapsedRealtime()
            val lastTime = lastProcessedTimes[it.sensor.type] ?: 0L
            if (currentTime - lastTime >= 500) {
                lastProcessedTimes[it.sensor.type] = currentTime
                onSensorData(sensorDataDomainMapper.toDomain(it))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.d("Accuracy changed for ${sensor?.name}: $accuracy")
    }
}
