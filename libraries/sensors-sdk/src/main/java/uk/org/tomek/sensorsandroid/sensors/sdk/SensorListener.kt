package uk.org.tomek.sensorsandroid.sensors.sdk

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorListener(
    context: Context,
    private val onSensorData: (SensorEvent) -> Unit
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

    fun startListening() {
        sensors.forEach { sensor ->
            val supported = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            if (!supported) {
                Log.w("SensorListener", "Sensor ${sensor.name} not supported")
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { onSensorData(it) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorListener", "Accuracy changed for ${sensor?.name}: $accuracy")
    }
}
