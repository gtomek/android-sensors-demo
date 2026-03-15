package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class BleData(
    val timestamp: Long,
    val deviceAddress: String,
    val deviceName: String?,
    val rssi: Int,
    val txPower: Int?,
    val manufacturerData: Map<Int, ByteArray>,
    val serviceUuids: List<String>,
    val advertisingIntervalMillis: Long?,
    val scanMode: Int,
    val scanDurationMillis: Long?,
    val beaconInfo: BeaconInfo? = null
)

sealed interface BeaconInfo {
    data class IBeacon(
        val proximityUuid: String,
        val major: Int,
        val minor: Int,
        val txPower: Int
    ) : BeaconInfo

    sealed interface Eddystone : BeaconInfo {
        data class Uid(val namespace: String, val instance: String) : Eddystone
        data class Url(val url: String) : Eddystone
        data class Tlm(
            val version: Int,
            val batteryVoltage: Int,
            val temperature: Float,
            val pduCount: Long,
            val uptime: Long
        ) : Eddystone
    }
}
