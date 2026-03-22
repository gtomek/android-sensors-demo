package uk.org.tomek.sensorsandroid.ui.model

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Data(
        val sensorData: List<SensorDataUiModel>,
        val wifiData: List<WifiDataUiModel> = emptyList(),
        val bleData: List<BleDataUiModel> = emptyList(),
        val mobileNetworkData: MobileNetworkDataUiModel? = null,
        val barometerData: BarometerDataUiModel? = null,
        val activityData: ActivityDataUiModel? = null,
        val deviceInfo: DeviceInfoUiModel? = null,
        val locationMessage: String? = null
    ) : MainUiState

    sealed interface Error : MainUiState {
        data class Generic(val message: String) : Error
        data class Permissions(val permissions: List<String>) : Error
    }
}
