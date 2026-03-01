package uk.org.tomek.sensorsandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
): ViewModel() {

    init {
        sensorsRepository.sensorDataFlow
            .onEach {
                Timber.v("Sensor data: $it")
            }
            .launchIn(viewModelScope)
    }

    fun startSensors() = sensorsRepository.startSensors()
    fun stopSensors() = sensorsRepository.stopLeasingSensors()
}