package uk.org.tomek.sensorsandroid.ui.model

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Data(
        val sensorData: List<SensorDataUiModel>,
        val wifiData: List<WifiDataUiModel> = emptyList(),
        val bleData: List<BleDataUiModel> = emptyList(),
        val mobileNetworkData: MobileNetworkDataUiModel? = null,
        val locationMessage: String? = null
    ) : MainUiState
}
