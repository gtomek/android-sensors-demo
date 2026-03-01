package uk.org.tomek.sensorsandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
    private val sensorDataMapper: SensorDomainUiMapper
): ViewModel() {

    val uiState = MutableStateFlow<List<SensorDataUiModel>>(emptyList())

    init {
        sensorsRepository.sensorDataFlow
            .onEach { sensorData ->
                Timber.v("Sensor data: $sensorData")
                uiState.update {
                    it + sensorDataMapper.toUi(sensorData)
                }
            }
            .launchIn(viewModelScope)
    }

    fun startSensors() = sensorsRepository.startSensors()
    fun stopSensors() = sensorsRepository.stopLeasingSensors()
}