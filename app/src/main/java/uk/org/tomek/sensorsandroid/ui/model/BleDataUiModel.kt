package uk.org.tomek.sensorsandroid.ui.model

data class BleDataUiModel(
    val deviceAddress: String,
    val deviceName: String?,
    val rssi: Int,
    val txPower: Int?,
    val beaconInfo: String?,
    val timestamp: String
)
