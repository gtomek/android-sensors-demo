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
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper
import uk.org.tomek.sensorsandroid.ui.model.DisplayType
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
    private val sensorDataMapper: SensorDomainUiMapper
): ViewModel() {

    private val _uiState = MutableStateFlow<List<SensorDataUiModel>>(emptyList())
    val uiState: StateFlow<List<SensorDataUiModel>> = _uiState.asStateFlow()

    private val displayType = MutableStateFlow(DisplayType.LIST)

    init {
        sensorsRepository.sensorDataFlow
            .onEach { sensorData ->
                Timber.v("Sensor data: $sensorData")
                val uiModel = sensorDataMapper.toUi(sensorData)
                _uiState.update { currentList ->
                    if (displayType.value == DisplayType.LAST_ONLY) {
                        val newList = currentList.toMutableList()
                        val index = newList.indexOfFirst { it.sensorName == uiModel.sensorName }
                        if (index != -1) {
                            newList[index] = uiModel
                        } else {
                            newList.add(uiModel)
                        }
                        newList
                    } else {
                        currentList + uiModel
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
            _uiState.update { currentList ->
                currentList.groupBy { it.sensorName }
                    .map { it.value.last() }
            }
        }
    }
}
