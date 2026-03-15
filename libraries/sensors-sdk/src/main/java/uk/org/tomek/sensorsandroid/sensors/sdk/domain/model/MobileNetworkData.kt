package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class MobileNetworkData(
    val timestamp: Long,
    val primaryCell: CellInfo?,
    val neighboringCells: List<CellInfo>
)

data class CellInfo(
    val type: String, // NR, LTE, WCDMA, GSM, etc.
    val cellId: Int?,
    val lac: Int?, // LAC for GSM/WCDMA, TAC for LTE
    val mcc: String?,
    val mnc: String?,
    val signalStrength: Int?, // dBm
    val timingAdvance: Int?
)
