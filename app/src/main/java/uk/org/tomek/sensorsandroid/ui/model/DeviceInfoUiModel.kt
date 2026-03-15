package uk.org.tomek.sensorsandroid.ui.model

data class DeviceInfoUiModel(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val batteryInfo: String,
    val screenState: String,
    val orientation: String,
    val powerSaveMode: String,
    val foregroundApp: String,
    val sensorCount: Int
)
