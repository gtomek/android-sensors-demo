package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class DeviceInfo(
    val metadata: DeviceMetadata,
    val context: DeviceContext
)

data class DeviceMetadata(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val sdkVersion: Int,
    val sensors: List<SensorMetadata>
)

data class SensorMetadata(
    val name: String,
    val vendor: String,
    val type: Int,
    val resolution: Float,
    val power: Float,
    val version: Int
)

data class DeviceContext(
    val isScreenOn: Boolean,
    val orientation: Int, // Surface.ROTATION_0, 90, 180, 270
    val batteryLevel: Float, // 0.0 to 1.0
    val isCharging: Boolean,
    val isPowerSaveMode: Boolean,
    val foregroundApp: String?
)
