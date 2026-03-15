package uk.org.tomek.sensorsandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper
import uk.org.tomek.sensorsandroid.ui.model.DisplayType
import uk.org.tomek.sensorsandroid.ui.model.MainUiState

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
    private val locationRepository: LocationRepository,
    private val sensorDataMapper: SensorDomainUiMapper
): ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val displayType = MutableStateFlow(DisplayType.LIST)

    init {
        // Initialize with empty data after "Loading"
        _uiState.value = MainUiState.Data(sensorData = emptyList())

        sensorsRepository.sensorDataFlow
            .onEach { sensorData ->
                Timber.v("Sensor data: $sensorData")
                val uiModel = sensorDataMapper.toUi(sensorData)
                _uiState.update { currentState ->
                    if (currentState is MainUiState.Data) {
                        val currentList = currentState.sensorData
                        val newList = if (displayType.value == DisplayType.LAST_ONLY) {
                            val updatedList = currentList.toMutableList()
                            val index = updatedList.indexOfFirst { it.sensorName == uiModel.sensorName }
                            if (index != -1) {
                                updatedList[index] = uiModel
                            } else {
                                updatedList.add(uiModel)
                            }
                            updatedList
                        } else {
                            currentList + uiModel
                        }
                        currentState.copy(sensorData = newList.sortedBy { it.sensorType })
                    } else {
                        currentState
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun startSensors() = sensorsRepository.startSensors()
    fun stopSensors() = sensorsRepository.stopLeasingSensors()

    fun changeDisplayType() {
        displayType.update {
            if (it == DisplayType.LIST) DisplayType.LAST_ONLY else DisplayType.LIST
        }
        if (displayType.value == DisplayType.LAST_ONLY) {
            _uiState.update { currentState ->
                if (currentState is MainUiState.Data) {
                    val filteredList = currentState.sensorData.groupBy { it.sensorName }
                        .map { it.value.last() }
                        .sortedBy { it.sensorType }
                    currentState.copy(sensorData = filteredList)
                } else {
                    currentState
                }
            }
        }
    }

    fun getCurrentLocation() {
        locationRepository.getLastKnownLocation()
            .onSuccess { location ->
                updateLocationMessage("Location: ${location.latitude}, ${location.longitude}")
            }
            .onFailure { error ->
                updateLocationMessage("Error: ${error.message}")
            }
    }

    private fun updateLocationMessage(message: String) {
        _uiState.update { currentState ->
            if (currentState is MainUiState.Data) {
                currentState.copy(locationMessage = message)
            } else {
                currentState
            }
        }
    }

    fun onLocationMessageShown() {
        _uiState.update { currentState ->
            if (currentState is MainUiState.Data) {
                currentState.copy(locationMessage = null)
            } else {
                currentState
            }
        }
    }
}
