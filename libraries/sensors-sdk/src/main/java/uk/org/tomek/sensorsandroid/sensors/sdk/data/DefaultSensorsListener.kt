package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import timber.log.Timber
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.SensorsListener

internal class DefaultSensorsListener(
    context: Context,
) : SensorEventListener, SensorsListener {
    private val onSensorData: (SensorEvent) -> Unit =  {
        Timber.d("Sensor data: ${it.values.joinToString()}")
    }
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

    override fun startListening() {
        sensors.forEach { sensor ->
            val supported = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (!supported) {
                Timber.Forest.w("Sensor ${sensor.name} not supported")
            }
        }
    }

    override fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { onSensorData(it) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Timber.Forest.d("Accuracy changed for ${sensor?.name}: $accuracy")
    }
}