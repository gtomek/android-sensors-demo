package uk.org.tomek.sensorsandroid.ui.model

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Data(
        val sensorData: List<SensorDataUiModel>,
        val locationMessage: String? = null
    ) : MainUiState
}
