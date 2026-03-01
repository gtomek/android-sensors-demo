package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.data.mapper.SensorDataDomainMapper
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorData
import kotlin.time.Duration.Companion.seconds

internal class DefaultSensorsListener(
    context: Context,
    private val sensorDataDomainMapper: SensorDataDomainMapper = SensorDataDomainMapper(),
) : SensorEventListener, SensorsListener {
    private val onSensorData: (SensorData) -> Unit = {
        Timber.d("Sensor data: ${it.sensorName} ${it.sensorValues}")
    }
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
    private val lastProcessedTimes = mutableMapOf<Int, Long>()

    override fun startListening() {
        sensors.forEach { sensor ->
            // Request 1 second delay (1,000,000 microseconds)
            val supported = sensorManager.registerListener(this, sensor, 1.seconds.inWholeMicroseconds.toInt())
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
            if (currentTime - lastTime >= 1000) {
                lastProcessedTimes[it.sensor.type] = currentTime
                onSensorData(sensorDataDomainMapper.toDomain(it))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.d("Accuracy changed for ${sensor?.name}: $accuracy")
    }
}
