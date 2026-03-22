package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.view.Display
import android.view.WindowManager
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.DeviceInformation
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceContext
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.DeviceMetadata
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorMetadata

internal class DefaultDeviceInformation(private val context: Context) : DeviceInformation {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    override fun getDeviceInformation(): DeviceInfo {
        return DeviceInfo(
            metadata = getMetadata(),
            context = getContext()
        )
    }

    private fun getMetadata(): DeviceMetadata {
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL).map { sensor ->
            SensorMetadata(
                name = sensor.name,
                vendor = sensor.vendor,
                type = sensor.type,
                resolution = sensor.resolution,
                power = sensor.power,
                version = sensor.version
            )
        }

        return DeviceMetadata(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT,
            sensors = sensors
        )
    }

    private fun getContext(): DeviceContext {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        val batteryLevel = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (scale > 0) level / scale.toFloat() else -1f
        } ?: -1f

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val display: Display? = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    context.display
                } catch (e: UnsupportedOperationException) {
                    displayManager.getDisplay(Display.DEFAULT_DISPLAY)
                }
            } else {
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }
        } catch (e: Exception) {
            displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        }

        val orientation = display?.rotation ?: -1

        val isScreenOn = powerManager.isInteractive

        val isPowerSaveMode = powerManager.isPowerSaveMode

        val foregroundApp = getForegroundApp()

        return DeviceContext(
            isScreenOn = isScreenOn,
            orientation = orientation,
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            isPowerSaveMode = isPowerSaveMode,
            foregroundApp = foregroundApp
        )
    }

    private fun getForegroundApp(): String? {
        return try {
            val tasks = activityManager.runningAppProcesses
            tasks?.firstOrNull { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }?.processName
        } catch (e: Exception) {
            null
        }
    }
}
