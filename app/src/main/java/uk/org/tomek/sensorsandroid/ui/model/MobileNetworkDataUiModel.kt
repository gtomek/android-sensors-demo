package uk.org.tomek.sensorsandroid.ui.model

data class MobileNetworkDataUiModel(
    val timestamp: String,
    val primaryCell: CellInfoUiModel?,
    val neighboringCells: List<CellInfoUiModel>
)

data class CellInfoUiModel(
    val type: String,
    val cellId: String,
    val lacTac: String,
    val mccMnc: String,
    val signalStrength: String,
    val timingAdvance: String
)
